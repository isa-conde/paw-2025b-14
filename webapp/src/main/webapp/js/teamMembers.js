document.addEventListener("DOMContentLoaded", () => {
    const memberInput = document.getElementById("memberInput");
    const datalist = document.getElementById("userSuggestions");
    const addMemberBtn = document.getElementById("addMemberBtn");
    const chipContainer = document.getElementById("chipContainer");
    const form = document.getElementById("teamForm");
    let timeoutId;

    async function fetchSuggestions(query) {
        if (query.length < 3) return;
        try {
            const res = await fetch(`${contextPath}/users/search?name=${encodeURIComponent(query)}`);
            const users = await res.json();
            updateDatalist(users);
        } catch (err) {
            console.error("Error al obtener sugerencias:", err);
        }
    }

    function updateDatalist(users) {
        datalist.innerHTML = "";
        users.forEach(user => {
            const option = document.createElement("option");
            option.value = user;
            datalist.appendChild(option);
        });
    }

    function addMember(name) {
        if (!name.trim()) return;

        const chip = document.createElement("div");
        chip.classList.add("chip");
        chip.textContent = name;

        const closeBtn = document.createElement("span");
        closeBtn.textContent = "X";
        closeBtn.classList.add("chip-close");
        closeBtn.onclick = function () {
            chipContainer.removeChild(chip);
            form.removeChild(hiddenInput);
        };

        chip.appendChild(closeBtn);
        chipContainer.appendChild(chip);

        const hiddenInput = document.createElement("input");
        hiddenInput.type = "hidden";
        hiddenInput.name = "members";
        hiddenInput.value = name;

        form.appendChild(hiddenInput);
    }

    addMemberBtn.addEventListener("click", () => {
        addMember(memberInput.value);
        memberInput.value = "";
    });

    memberInput.addEventListener("input", (e) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            fetchSuggestions(e.target.value.trim());
        }, 300);
    });

    memberInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            addMember(memberInput.value);
            memberInput.value = "";
            datalist.innerHTML = "";
        }
    });
});
