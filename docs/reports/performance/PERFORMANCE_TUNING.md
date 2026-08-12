# Performance Tuning Postmortem: Zipkin Instability Under Load

**Location:** `docs/reports/performance/PERFORMANCE_TUNING.md`
**Audience:** Software architects, backend engineers, technical interviewers
**Scope:** API Gateway, Config Server, Eureka Server, User Service, Rating Service, Hotel Service
**Observability:** Micrometer Tracing, Brave, Zipkin
**Load Testing Tool:** k6

**Evidence basis:** `LOAD_TESTING.md` and `DISTRIBUTED_TRACING.md` are the authoritative sources for the benchmark and tracing facts referenced throughout this document; `PERFORMANCE_ANALYSIS.md` is the authoritative source for the architectural reasoning it references. Where this postmortem's own investigation narrative is not independently corroborated by those three documents, it is marked as such rather than presented as confirmed fact.

---

## Executive Summary

During the k6 load-testing campaign, the benchmark evidence shows a first observed reliability degradation at **250 VU / 80s (90.69% success, 456 failed requests)**, with non-monotonic behaviour at higher concurrency (`LOAD_TESTING.md` §7.3). Separately, runtime investigation of the observability stack found that the Zipkin process — the tracing backend receiving spans from every service in the request chain — experienced JVM heap exhaustion, with its logs recording `OutOfMemoryError: Java heap space` (`DISTRIBUTED_TRACING.md` §8).

**These two findings are related in that they were investigated together, but the available evidence does not establish that the Zipkin OOM event caused the k6 request failures.** Zipkin is not a synchronous dependency in the client request path (`GET /users` does not call Zipkin and does not wait on it); a Zipkin outage stops trace collection, not necessarily the client-facing response. `LOAD_TESTING.md` §8.4 is explicit that a causal relationship between a specific runtime event and a specific benchmark failure would require timestamp-correlated application and benchmark logs, which are not part of the current evidence set. This document preserves that distinction rather than resolving it.

What this postmortem documents, accurately: an observed Zipkin JVM heap-exhaustion event, the investigation that identified it, a proposed remediation (JVM heap adjustment), and what would be required to validate that remediation — which has not yet been done with controlled before/after evidence.

---

## Observed Symptoms

Two categories of symptoms are relevant here, and it is important to keep them separate rather than treat them as a single confirmed narrative:

**Evidenced in the finalized reports:**

- Rising k6 request failures at higher concurrency, first measurable at 250 VU / 80s (90.69% success, 456 failed), with further, non-monotonic degradation at 275–300 VU (`LOAD_TESTING.md` §6, §7).
- A Zipkin process log recording `Terminating due to java.lang.OutOfMemoryError: Java heap space` (`DISTRIBUTED_TRACING.md` §8).
- Captured traces during the investigation period showed differing span counts (96 spans in one capture, 152 in another) for the same logical `GET /users` request. `DISTRIBUTED_TRACING.md` §5.2 is explicit that the cause of this difference is not established by the available evidence, and this document does not attempt to supply one.

**Noted during the original investigation but not independently confirmed in the finalized benchmark or tracing evidence:**

- The Zipkin UI becoming slow or unresponsive during the test window.
- Specific instances of incomplete or gapped trace trees (as distinct from the differing span counts above, which are documented).

These second-category observations are retained here for investigative context, since they plausibly motivated the direction of the investigation, but they should not be read as independently verified facts — no screenshot or log evidence of Zipkin UI unresponsiveness or of gapped trace trees is present in `LOAD_TESTING.md` or `DISTRIBUTED_TRACING.md`.

---

## Investigation Process

### Log Analysis

The finalized evidence set confirms a Zipkin JVM heap-exhaustion event:

`OutOfMemoryError: Java heap space`

No equivalent JVM heap-exhaustion event is documented in the finalized tracing or benchmark reports for User Service, Rating Service, or Hotel Service.

The available evidence therefore establishes the Zipkin OOM as an observed infrastructure event, but does not establish it as the root cause of the client-facing benchmark failures.

### Zipkin JVM Heap Exhaustion

`DISTRIBUTED_TRACING.md` §8 documents the captured Zipkin log directly: the collector process terminated shortly after startup with `Terminating due to java.lang.OutOfMemoryError: Java heap space`, dated `2026-07-22T15:37:01`. This is an observed, logged event.

What is **not** established by this log entry alone is a specific mechanism connecting it to sustained high-concurrency trace ingestion during a particular k6 run — the log shows the process terminating shortly after startup, which is consistent with several possible triggers (undersized heap relative to any sustained span volume, a restart into an already-constrained container limit, etc.). This document does not assert a single confirmed trigger beyond the observed `OutOfMemoryError`.

### Infrastructure vs. Application Bottleneck

The finalized evidence set contains a documented Zipkin JVM heap-exhaustion event. It does not contain an equivalent documented JVM heap-exhaustion event for User Service, Rating Service, or Hotel Service.

This does not establish that the application services were free of other resource constraints; no thread-pool, database, connection-pool, or JVM resource metrics were captured during the benchmark campaign.

The Zipkin OOM event and the benchmark's first observed degradation (250 VU / 80s) fall within the broader investigation period, but no timestamp-level correlation between a specific k6 run and the Zipkin OOM is established.

---

## Observed Failure Mechanism

Zipkin's JVM raised `OutOfMemoryError: Java heap space` and the process terminated. This is the observed failure mechanism for the Zipkin process specifically, described here at the level the evidence supports:

- **Evidenced:** the Zipkin JVM exhausted its available heap and the process logged the resulting `OutOfMemoryError`.
- **Reasonable engineering inference:** distributed tracing produces a span per service hop per request (`DISTRIBUTED_TRACING.md` §4), so as concurrent request volume rises, span-ingestion volume into Zipkin rises with it, which is a generic mechanism by which memory pressure on a trace collector can increase under load. This is offered as a plausible contributing factor to why heap pressure could build, not as a measured relationship — no span-volume-to-memory-usage measurement exists in the reviewed evidence.
- **Not established:** an exact heap requirement for this workload, an exact span-to-memory ratio, or a causal chain from this OOM event to any specific k6-reported failed request.

**This document distinguishes the observed Zipkin failure mechanism from the benchmark's root cause.** The available evidence establishes the Zipkin heap-exhaustion event but does not conclusively establish a single root cause for the client-facing benchmark failures recorded starting at 250 VU / 80s. Sequential downstream aggregation in User Service (User Service calling Rating Service, then Hotel Service, due to a data dependency rather than a chained architecture — see `PERFORMANCE_ANALYSIS.md`) remains an independently plausible architectural contributor to latency and, at sufficient concurrency, to failures, and is not ruled out by anything in this investigation.

---

## Contributing Factors

The following are documented as plausible contributing factors to the observed Zipkin heap exhaustion, not as a proven causal chain to the benchmark failures:

- **Distributed tracing overhead.** Every request through the traced path (API Gateway, User Service, Rating Service, Hotel Service — a fan-out from User Service to its two downstream services, not a Gateway → User → Rating → Hotel chain; see `PERFORMANCE_ANALYSIS.md` and `DISTRIBUTED_TRACING.md` §3.1) produces multiple spans per hop, including the per-service security spans (`authenticate bearertoken`, `authorize request`) that `DISTRIBUTED_TRACING.md` confirms are generated independently by each service. This is inherent to how the tracing implementation works, not a defect.
- **Span volume under concurrency.** As concurrent request volume increases, the rate of span generation increases correspondingly, since Zipkin must hold and process incoming spans. This is a reasonable engineering inference about the general mechanism, not a measured figure for this deployment.
- **JVM heap configuration.** The Zipkin process's JVM heap configuration at the time of the observed OOM event was not documented in the reviewed evidence (no `JAVA_OPTS` or container memory-limit configuration was captured alongside the OOM log). Whether it was running with framework/platform defaults or an explicit-but-undersized configuration is not established.

---

## Engineering Decision

Given the observed Zipkin heap exhaustion, the practical options considered were: (a) adjust the Zipkin JVM heap configuration, or (b) treat this as a signal to redesign the observability ingestion path (e.g., a queue in front of span ingestion, an alternate storage backend, or horizontal scaling of Zipkin).

Heap adjustment is proposed as the first response, for the following reasons:

- The observed failure mode (`OutOfMemoryError`) is consistent with a memory-capacity constraint, which a heap adjustment directly targets, without requiring changes to the tracing architecture or any service code.
- It is a low-risk, fast, reversible change relative to redesigning the observability pipeline.
- A larger architectural investment in the observability pipeline (queuing, horizontal scaling, alternate storage) is a reasonable next step only if a capacity adjustment is confirmed insufficient — jumping directly to a redesign without first evaluating the simpler fix would be premature.

This should be read as a proposed first iteration, not a decision already validated by a before/after test — see Validation below.

---

## Proposed Remediation

A JVM heap adjustment for the Zipkin process is proposed:

```
JAVA_OPTS="-Xmx2048m"
```

**`-Xmx2048m` — Maximum heap size.** Sets the ceiling the JVM's heap may grow to under memory pressure to 2048 MB. This is the parameter most directly aimed at the observed `OutOfMemoryError`, on the reasoning that a higher ceiling gives the garbage collector more room to work with before heap exhaustion recurs at a similar load level.

**Status of this remediation: proposed only.** No configuration file, deployment manifest, or container definition confirming this (or any other specific heap configuration) was actually applied to the Zipkin process is present in the reviewed evidence. This document does not claim the change has been deployed. It is documented here as the concrete proposed adjustment, pending application and validation.

---

## Validation

**No controlled before/after benchmark evidence for this remediation is present in the reviewed documents.** `LOAD_TESTING.md` and `DISTRIBUTED_TRACING.md` do not contain a re-run of the same k6 profile against a Zipkin instance running with an adjusted heap configuration, and no such comparison is asserted here.

A like-for-like validation run is required to establish whether the proposed heap adjustment resolves the observed Zipkin instability. That validation would need to:

- Use the same k6 workload configuration that produced the first observed degradation (250 VU / 80s), for a like-for-like comparison.
- Confirm whether the Zipkin process remains free of `OutOfMemoryError` under that same load.
- Record k6 success/failure metrics from that run independently, since — per the Executive Summary above — even a stable Zipkin process would not, by itself, confirm that Zipkin was the cause of the original benchmark failures. A validation run that still shows failures at 250 VU / 80s despite a stable Zipkin would indicate the heap adjustment did not address the client-facing issue, and would point back toward the architectural factors discussed in `PERFORMANCE_ANALYSIS.md` (e.g. sequential aggregation) as more likely contributors.

Until that validation exists, this document treats the proposed remediation's effect on both Zipkin stability and benchmark success rate as unverified.

---

## Performance Trade-offs

Raising the JVM heap ceiling is a standard trade-off worth stating even before validation data exists, so the expected trade-off is understood going in:

- A larger maximum heap does not reduce garbage-collection activity by itself; a JVM managing a larger heap can, in principle, spend more wall-clock time per garbage-collection cycle when collections occur, even if those cycles happen less frequently or less catastrophically under pressure. No specific latency or memory measurement from this project's Zipkin instance is available to quantify this — it is stated as a general JVM-tuning consideration, not a project-specific measured effect.
- Increasing available heap addresses a JVM resource constraint; it does not, by itself, address or rule out the application-level architectural factors discussed in `PERFORMANCE_ANALYSIS.md` (sequential downstream aggregation, blocking Feign calls). Heap tuning and architectural optimization are separate levers, and confirming one does not confirm or replace the other.
- If validation confirms the heap adjustment resolves Zipkin's OOM behaviour under the same load, and the benchmark's request-level success rate also recovers under an unchanged Zipkin/observability path, that would meaningfully narrow the diagnosis. Until that validation exists, this remains a proposed adjustment with an expected — not measured — trade-off profile.

---

## Lessons Learned

- **Infrastructure monitoring should cover the observability stack itself, not only the business services.** The Zipkin process's own memory health was not being tracked in this investigation; the first indication of trouble was an OOM entry found in logs after the fact, not an early warning from a metric.
- **JVM tuning is a first-class concern for any JVM-based infrastructure component**, including tooling like Zipkin that the team operates but did not author. Default JVM memory settings should not be assumed adequate for a given trace volume without validation.
- **Correlation identified during an investigation is not the same as an established causal relationship.** The Zipkin OOM event and the benchmark's reliability degradation were investigated together because they occurred within the same broader period; that is a reasonable basis for investigation, not a substitute for timestamp-correlated evidence establishing causation. This document intentionally keeps that distinction explicit rather than resolving it prematurely.
- **A postmortem's proposed remediation is not the same as a validated one.** Proposing a JVM heap adjustment based on an observed `OutOfMemoryError` is a reasonable first response; treating it as resolved before a controlled before/after test exists would overstate what has actually been demonstrated.

---

## Future Improvements

- **Perform the validation run.** This is the immediate next step ahead of any other future work listed here — apply the proposed heap adjustment (or an evidence-informed alternative) and re-run the same k6 profile that first showed degradation at 250 VU / 80s, capturing both Zipkin process logs and k6 results from that run.
- **Timestamp-correlated analysis.** Per `LOAD_TESTING.md` §11, correlate benchmark execution timestamps with runtime traces and Zipkin logs when matching evidence is available, to determine whether the Zipkin OOM event and any specific failed k6 requests actually overlap in time.
- **Prometheus** — export JVM and process-level metrics from Zipkin itself (heap usage, GC activity, span-ingestion rate) so memory pressure is visible before it results in an OOM error.
- **Grafana** — dashboards over those metrics, alongside the business services' own metrics, for a unified operational view.
- **JVM monitoring** — extend proactive JVM health monitoring (heap, GC pause times, thread counts) to every JVM-based component in the platform, not only the services under direct development.
- **Container resource limits** — where these services run in containers, ensure container memory limits are explicitly set and kept consistent with any JVM `-Xmx` value, so the two layers do not work against each other.
- **Distributed tracing optimization** — investigate sampling strategies to reduce span volume at high concurrency where full-fidelity tracing on every request is not strictly necessary, as a complement to, not a replacement for, adequately sized infrastructure.
- **Architectural investigation in parallel.** Because this document does not establish Zipkin as the sole or proven cause of the benchmark failures, the sequential-aggregation and concurrency factors discussed in `PERFORMANCE_ANALYSIS.md` should continue to be investigated independently rather than deprioritized on the assumption that the observability-tier finding fully explains the benchmark results.
