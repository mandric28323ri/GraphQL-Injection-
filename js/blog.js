function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

function goBack() {
    window.location.href = "app.html";
}

async function loadBlog() {
    const blogId = getQueryParam("blogId");

    if (!blogId) {
        alert("Missing blog ID.");
        return;
    }

    const query = `
        query {
            postById(id: "${blogId}") {
                id
                title
                content
                category
                author { username }
            }
        }
    `;

    try {
        const data = await graphqlRequest(query);

        if (!data.postById) {
            alert("Blog not found.");
            return;
        }

        const post = data.postById;

        document.getElementById("blogTitle").textContent = post.title;
        document.getElementById("blogCategory").textContent = post.category;
        document.getElementById("blogAuthor").textContent = post.author.username;
        document.getElementById("blogContent").value = post.content;

    } catch(err) {
        console.error("Error loading blog:", err);
    }
}

async function updateBlog() {
    const blogId = getQueryParam("blogId");
    const content = document.getElementById("blogContent").value;

    const query = `
        mutation {
            updatePost(id: "${blogId}", input: {
                content: "${content.replace(/"/g, '\\"')}"
            }) {
                id
                title
                content
            }
        }
    `;

    try {
        await graphqlRequest(query);
        alert("Blog updated successfully!");
    } catch(err) {
        alert("Greška: " + err.message);
    }
}

document.addEventListener("DOMContentLoaded", loadBlog);