document.addEventListener("DOMContentLoaded", () => {
    const memberInput = document.getElementById("memberInput");
    const datalist = document.getElementById("userSuggestions");
    const addMemberBtn = document.getElementById("addMemberBtn");
    const chipContainer = document.getElementById("chipContainer");
    const form = document.getElementById("teamForm");

     memberInput.addEventListener("input", () => {
        const query = memberInput.value.trim().toLowerCase();
        datalist.innerHTML = "";
        if (query.length < 3) return;

        const matches = allUsers.filter(u => u.toLowerCase().includes(query));
        matches.forEach(name => {
            const option = document.createElement("option");
            option.value = name;
            datalist.appendChild(option);
        });
    });

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

        const index = allUsers.indexOf(newMember);
        if (index !== -1) {
            allUsers.splice(index, 1);
        }

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

    memberInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            addMember(memberInput.value);
            memberInput.value = "";
            datalist.innerHTML = "";
        }
    });
});
