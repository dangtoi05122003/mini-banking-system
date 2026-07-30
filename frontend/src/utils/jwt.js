import {jwtDecode} from "jwt-decode";

export function decodeToken(token) {
    const decoded = jwtDecode(token);
    const currentTime = Date.now() / 1000;
    if (decoded.exp && decoded.exp < currentTime) {
        return null;
    }
    return decoded;
}