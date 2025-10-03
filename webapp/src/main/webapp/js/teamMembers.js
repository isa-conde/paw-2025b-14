document.addEventListener("DOMContentLoaded", () => {
    const memberInput = document.getElementById("memberInput");
    const addMemberBtn = document.getElementById("addMemberBtn");
    const chipContainer = document.getElementById("chipContainer");
    const form = document.getElementById("teamForm");
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
        hiddenInput.name = "members"; // 👈 este name es la clave
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
        }
    });
});
