# Decision-001

## Context

GET /users aggregates ratings and hotels
for every user.

## Initial Design

Sequential invocation using
getUserWithResilience().

## Evidence

Zipkin:

124 spans

Latency:

1.86 sec

Logs:

RateLimiter fallback triggered.

## Conclusion

Implementation is functionally correct
but introduces a linear increase in
downstream service calls.

Future work:

Redesign aggregation strategy.