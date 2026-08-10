import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 300,
    duration: '30s',
};

export default function () {

    const params = {
        headers: {
            Authorization: 'Bearer eyJraWQiOiJjaU95Zi1EZk5PUFVJRm1XRDIxZ3Naa1h1blBkVzYxbXkzRlVEZW40TVhJIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULkpjYmI0NEtCN0dwcVFWdFhvbjZ6bjlQYW0xbHYyZGU3ZmluLW5FLUZ4YW8ub2FyNGQxdXB1cGlBZHdWbnk2OTciLCJpc3MiOiJodHRwczovL2ludGVncmF0b3ItNTIwMjkwMS5va3RhLmNvbS9vYXV0aDIvZGVmYXVsdCIsImF1ZCI6ImFwaTovL2RlZmF1bHQiLCJpYXQiOjE3ODYyMTk3ODMsImV4cCI6MTc4NjI0ODU4MywiY2lkIjoiMG9hMTB6Y2N3ZGNCUmp5cmI2OTgiLCJ1aWQiOiIwMHUxMHhvYzZyd1RPWE8yTTY5OCIsInNjcCI6WyJvZmZsaW5lX2FjY2VzcyIsImVtYWlsIiwiZ3JvdXBzIiwib3BlbmlkIiwicHJvZmlsZSJdLCJhdXRoX3RpbWUiOjE3ODYyMTk3NTcsInN1YiI6Im5jMjRjczYyQHRoZGNpaGV0LmFjLmluIiwiZ3JvdXBzIjpbIkV2ZXJ5b25lIiwiQWRtaW4iXX0.RhLLZGthsvL1qFNQhyP_-rGGWEN2JlCZJqmlqkLZ-QVoNi0t7Wixy3vZhG36PkJTz-ng1e8nSurUfnWiGA4V8jkqO5X6ZNqz6-eXqk_1ChcYPd-go1mpZ93JNl8a6GK-0heS4mA9YAEXAOHb58i5o87SDps_qnINfKLf6ts4A2Ew1DNg_4d3LHylVyo2M4TnBp5jzQ8onqLJdoOfZI-eUI8Kwx_J92YxHw4RnmyeXVBCjoQxT7jzmNFxMgJs8W1GoYJcGZcXuhfG6AnboXBO4iihCdsBoI2B1FPh8vnikerIfMAqExHk9Y4GjlibQBAvXggPMu6Ej7RHoKomNwABvw'
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