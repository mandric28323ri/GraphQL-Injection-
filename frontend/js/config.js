const API_URL = "http://localhost:8080";
const GRAPHQL_URL = `${API_URL}/graphql`;

async function graphqlRequest(query) {
    const token = getToken();
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch(GRAPHQL_URL, {
        method: "POST",
        headers: headers,
        body: JSON.stringify({ query })
    });
    const data = await response.json();
    if (data.errors) throw new Error(data.errors[0].message);
    return data.data;
}