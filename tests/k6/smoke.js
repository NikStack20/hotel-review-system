import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 50,
    duration: '30s',
};

export default function () {

    const params = {
        headers: {
            Authorization: 'Bearer eyJraWQiOiJjaU95Zi1EZk5PUFVJRm1XRDIxZ3Naa1h1blBkVzYxbXkzRlVEZW40TVhJIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULm9tQVBUTWVCUndkTGg1NG1KZVJNOW96SEpxLUN2ZjFUZk9KVDlUbEl3QUkub2FyNGF0dmZicDZYUlgyalE2OTciLCJpc3MiOiJodHRwczovL2ludGVncmF0b3ItNTIwMjkwMS5va3RhLmNvbS9vYXV0aDIvZGVmYXVsdCIsImF1ZCI6ImFwaTovL2RlZmF1bHQiLCJpYXQiOjE3ODQ5MTE3MjksImV4cCI6MTc4NDk0MDUyOSwiY2lkIjoiMG9hMTB6Y2N3ZGNCUmp5cmI2OTgiLCJ1aWQiOiIwMHUxMHhvYzZyd1RPWE8yTTY5OCIsInNjcCI6WyJvZmZsaW5lX2FjY2VzcyIsImdyb3VwcyIsImVtYWlsIiwib3BlbmlkIiwicHJvZmlsZSJdLCJhdXRoX3RpbWUiOjE3ODQ5MTE3MTUsInN1YiI6Im5jMjRjczYyQHRoZGNpaGV0LmFjLmluIiwiZ3JvdXBzIjpbIkV2ZXJ5b25lIiwiQWRtaW4iXX0.QvMMmO9PlXBffexGUN2BKTXbP_3I1bjbEpS2S-fdKJdbud3y9zmjw7TkONc4oGUxqvVt8QGvO8GCbVk2_9N57qTktS8hpYuDSMgXZEfZCPpc3ld73RQZIy7_AsKX10pHunnBzUFJI9W46mhLEs8g0ffLCsFFBb8per3RVk_jxV3crPATMXp9aZio1_W6lFluQpQbppDuTdki2eNrXYxDjsP6NewGSpQ-3-E2roRwKmx3VPU-XUQtHjx0MawfjjurIKTwZq2eirwnn6CYpMg7746bpLCk2pTTmKkWuyE1B5K0ZU7MeHkPyC5cM4mEiRur-uwB7gyxAbqQ-4CrmxbNxQ'
        }
    };

    const res = http.get(
        'http://localhost:7053/users',
        params
    );

    check(res, {
        'Status is 200': (r) => r.status === 200,
    });
}