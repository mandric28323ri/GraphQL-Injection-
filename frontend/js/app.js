if (!getToken()) {
    window.location.href = "login.html";
}

const user = getUser();

if (user) {
    const userInfo = document.getElementById("userInfo");
    if (user.role === "ADMIN") {
        userInfo.innerHTML = `<a href="admin.html" style="color:#4f46e5;font-weight:bold;font-size:14px;text-decoration:none;">👤 ${user.username} (Admin)</a>`;
    } else {
        userInfo.innerHTML = `<span style="font-size:14px;color:#374151;">👤 ${user.username}</span>`;
    }
}

async function getBlog() {

    const id = blogId.value;

    const query = `
        query {
            postById(id: "${id}") {
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
            alert("Blog not found");
            return;
        }

        const post = data.postById;

        blogCard.style.display = "block";

        blogCard.innerHTML = `
            <h3>${post.title}</h3>
            <p><b>Author:</b> ${post.author.username}</p>
            <p><b>Category:</b> ${post.category}</p>
            <p>${post.content}</p>
        `;

    } catch(err) {
        alert("Greska: " + err.message);
    }
}

async function loadMyBlogs() {
    const user = getUser();
    const container = document.getElementById("myBlogs");

    const query = `
        query {
            posts {
                id
                title
                category
                author { username }
            }
        }
    `;

    try {
        const data = await graphqlRequest(query);

        const myPosts = data.posts.filter(p => p.author.username === user.username);

        if (!myPosts.length) {
            container.innerHTML = "<p style='color:#999;font-size:14px;'>Nema blogova</p>";
            return;
        }

        container.innerHTML = myPosts.map(p => `
            <div style="padding:10px 0; border-bottom:1px solid #f3f4f6; font-size:14px; cursor:pointer;"
                 onclick="window.location.href='blog.html?blogId=${p.id}'">
                    ${p.title} <span style="color:#999; font-size:12px;">[${p.category}]</span>
            </div>
        `).join("");

    } catch(err) {
        container.innerHTML = `<p style="color:red;font-size:14px;">${err.message}</p>`;
    }
}

loadMyBlogs();