function getToken() {
    return localStorage.getItem("token");
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function setUser(user) {
    localStorage.setItem("user", JSON.stringify(user));
}

function getUser() {
    const u = localStorage.getItem("user");
    return u ? JSON.parse(u) : null;
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = "login.html";
}

function authHeader() {
    return {
        "Authorization": "Bearer " + getToken()
    };
}

function handle401(response) {
    if (response.status === 401 || response.status === 403) {
        logout();
        return true;
    }
    return false;
}