if (getToken()) {
    window.location.href = "app.html";
}

async function login() {

    const query = `
        mutation {
            login(username: "${username.value}", password: "${password.value}") {
                token
                user { id username email role }
            }
        }
    `;

    try {
        const data = await graphqlRequest(query);

        if (!data.login || !data.login.token) {
            error.innerText = "Invalid credentials";
            return;
        }

        setToken(data.login.token);
        setUser(data.login.user);
        window.location.href = "app.html";

    } catch (err) {
        error.innerText = "Invalid credentials";
    }
}