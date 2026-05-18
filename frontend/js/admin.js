const user = getUser();

if (!user || user.role !== "ADMIN") {
    window.location.href = "app.html";
}

async function loadUsers() {
    const query = `query { users { id username email role } }`;

    try {
        const data = await graphqlRequest(query);
        renderUsers(data.users);
    } catch(err) {
        document.getElementById("usersTable").innerHTML = `<p style="color:red;">${err.message}</p>`;
    }
}

function renderUsers(users) {
    const container = document.getElementById("usersTable");

    if (!users.length) {
        container.innerHTML = "<p>Nema korisnika</p>";
        return;
    }

    let html = `<table style="width:100%;border-collapse:collapse;font-size:14px;">
        <thead>
            <tr style="background:#f3f4f6;text-align:left;">
                <th style="padding:10px;">ID</th>
                <th style="padding:10px;">Username</th>
                <th style="padding:10px;">Email</th>
                <th style="padding:10px;">Role</th>
                <th style="padding:10px;">Akcija</th>
            </tr>
        </thead>
        <tbody>`;

    users.forEach(u => {
        html += `<tr id="row-${u.id}" style="border-bottom:1px solid #f3f4f6;">
            <td style="padding:10px;">${u.id}</td>
            <td style="padding:10px;">${u.username}</td>
            <td style="padding:10px;">${u.email}</td>
            <td style="padding:10px;">${u.role}</td>
            <td style="padding:10px;">
                <button class="logout-btn" onclick="deleteUser(${u.id}, '${u.username}')">Obrisi</button>
            </td>
        </tr>`;
    });

    html += `</tbody></table>`;
    container.innerHTML = html;
}

async function deleteUser(id, username) {
    if (!confirm(`Obrisati korisnika "${username}"?`)) return;

    const query = `mutation { deleteUser(id: ${id}) }`;

    try {
        const data = await graphqlRequest(query);
        if (data.deleteUser) {
            document.getElementById("row-" + id).remove();
        } else {
            alert("Brisanje nije uspelo");
        }
    } catch(err) {
        alert("Greska: " + err.message);
    }
}

loadUsers();