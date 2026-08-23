import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 300,
    duration: '30s',
};

export default function () {

    const params = {
        headers: {
            Authorization: 'Bearer __ENV.OKTA_ACCESS_TOKEN'
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