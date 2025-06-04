import {AJAX, formatTimeMillis, isEmptyObject} from "../helper.js";

// TODO: get user info from DB
const users = [];
const newNote = document.querySelector("#newNote");
const recentNotes = document.querySelector("#recentNotesContainer");
const suggestions = document.querySelector("#mentionSuggestions");

window.addEventListener('load', e => {
    const data = getNotes();
    getContacts();

})

const getNotes = async function() {
    try {
        const data = await AJAX("/getnotes");
            const dataArr = Object.entries(data);
            dataArr.sort(([, a], [, b]) => parseInt(b.timeStamp) - parseInt(a.timeStamp));

        for (const [key, value] of dataArr.slice(0,5)) {

            recentNotes.innerHTML += `<div class="recent-notes-tab" data-noteid='${value.uid}'>
                                        <div class="note-content">${JSON.parse(value.noteContent)}</div>
                                            <div class="fa-solid fa-trash"></div>
                                        <div class="time-posted">${formatTimeMillis(value.timeStamp)}</div>
                                    </div>`;
        }

        document.querySelector('#btnViewNotes').addEventListener('click', () => {
            window.location.href = "/allnotes";
        });

        if(isEmptyObject(data)) {
            console.log("no data")
        }

    } catch (err) {
        console.error(err + ': Unable to load notes. Please try again.');
    }
}

const getContacts = async function() {
    try {
        const data = await AJAX("/getcontacts");
        const dataArr = Object.entries(data);

        dataArr.sort((a, b) => {
            const nameA = (a[1].surname || a[1].firstName || "").toLowerCase();
            const nameB = (b[1].surname || b[1].firstName || "").toLowerCase();
            return nameA.localeCompare(nameB);
        });



        for (const [key, value] of dataArr) {
            const contact = {};

            contact.name = `${value.firstName}  ${value.surname ?? ''}`.trim();
            contact.id = value.contactId;

            users.push(contact);

        }

        if(isEmptyObject(data)) {
            console.log("no data")
        }

    } catch (err) {
        console.error(err + ': Unable to load contacts. Please try again.');
    }
    console.log(users);
}


newNote.addEventListener("keyup", (e) => {
    const selection = window.getSelection();
    const range = selection.getRangeAt(0);
    const container = range.startContainer;

    // Get the text up to the cursor
    const text = container.textContent;
    const offset = range.startOffset;
    const beforeCursor = text.slice(0, offset);
    const match = beforeCursor.match(/@(\w*)$/);

    if (match) {
        const query = match[1].toLowerCase();
        const matchedUsers = users.filter(u => u.name.startsWith(query)).map(u => u.name);

        if (matchedUsers.length) {
            const rect = range.getBoundingClientRect();
            suggestions.style.top = rect.bottom + window.scrollY + "px";
            suggestions.style.left = rect.left + window.scrollX + "px";
            suggestions.innerHTML = matchedUsers.map(name => `<div>${name}</div>`).join("");
            suggestions.style.display = "block";

            Array.from(suggestions.children).forEach(child => {
                child.onclick = () => {

                    const mentionText = "@" + child.textContent;

                    // Create the span. Make sure it's not contenteditable.
                    const mentionEl = document.createElement("span");
                    mentionEl.className = "mention";
                    mentionEl.textContent = mentionText;
                    console.log(mentionText);
                    console.log(users.filter(u => u.name === mentionText.slice(1))[0]);
                    mentionEl.dataset.contactid = users.filter(u => u.name === mentionText.slice(1))[0].id;
                    mentionEl.contentEditable = "false"; // prevents typing into it

                    // Add a space after the mention
                    const spaceNode = document.createTextNode(" ");

                    // Replace the matched @text with the mention span
                    range.setStart(container, match.index);
                    range.setEnd(container, offset);
                    range.deleteContents();

                    // Insert the mention span and space
                    range.insertNode(spaceNode);
                    range.insertNode(mentionEl);

                    // Move caret after the space
                    const newRange = document.createRange();
                    newRange.setStartAfter(spaceNode);
                    newRange.setEndAfter(spaceNode);

                    const selection = window.getSelection();
                    selection.removeAllRanges();
                    selection.addRange(newRange);

                    suggestions.style.display = "none";
                    newNote.focus();

                };
            });
        } else {
            suggestions.style.display = "none";
        }
    } else {
        suggestions.style.display = "none";
    }

});

function highlightMentions(text) {
    return text.replace(/@([A-Za-z0-9._ -]+)/g, (_, mention) =>
        `<span class="mention">@${mention.replace(/ /g, '&nbsp;')}</span>`
    );
}

function getCaretCharacterOffsetWithin(element) {
    const selection = window.getSelection();
    let charCount = 0;
    if (selection.rangeCount > 0) {
        const range = selection.getRangeAt(0);
        const preCaretRange = range.cloneRange();
        preCaretRange.selectNodeContents(element);
        preCaretRange.setEnd(range.endContainer, range.endOffset);
        charCount = preCaretRange.toString().length;
    }
    return charCount;
}

function setCaretPosition(element, chars) {
    const range = document.createRange();
    const selection = window.getSelection();

    let charIndex = 0;
    let nodeStack = [element], node, foundStart = false;

    while ((node = nodeStack.pop()) && !foundStart) {
        if (node.nodeType === 3) { // Text node
            const nextCharIndex = charIndex + node.length;
            if (chars <= nextCharIndex) {
                range.setStart(node, chars - charIndex);
                range.collapse(true);
                foundStart = true;
                break;
            }
            charIndex = nextCharIndex;
        } else {
            let i = node.childNodes.length;
            while (i--) nodeStack.push(node.childNodes[i]);
        }
    }

    if (foundStart) {
        selection.removeAllRanges();
        selection.addRange(range);
    }
}

newNote.addEventListener("input", () => {
    const caretOffset = getCaretCharacterOffsetWithin(newNote);
    const plainText = newNote.innerText;

    // Highlight mentions
    //newNote.innerHTML = highlightMentions(plainText);

    // Restore caret
    setCaretPosition(newNote, caretOffset);
});

document.querySelector('#btnSubmitNewNote').addEventListener('click', async (e) => {
    const textContent = newNote.textContent;
    const textContentHTML = newNote.innerHTML;
    console.log(textContentHTML);
    const result = await AJAX('/submitnewnote', textContentHTML);
    console.log(result.uid);
    console.log(JSON.parse(result.uid));

    recentNotes.prepend(`<div class="recent-notes-tab" data-noteid='${JSON.parse(result.uid)}'>
                                <div class="note-content">${textContentHTML}</div>
                                <div class="fa-solid fa-trash"></div>
                                <div class="time-posted">Just now</div>
                                </div>`);
})

document.querySelector('#recentNotes').addEventListener('click', async (e) => {
    if (e.target.classList.contains('fa-trash') || e.target.tagName.toLowerCase() === 'path') {
        console.log('trash')
        const noteId = e.target.closest('.recent-notes-tab').getAttribute('data-noteid');
        console.log(noteId)
        const data = await AJAX(`/deletenote/${noteId}`, 'post');
        if (data === "SUCCESS") {
            e.target.closest('.recent-notes-tab').remove();
        } else {
            console.error("Unable to delete note.");
        }
    }
});

document.querySelector('.recent-notes').addEventListener('click', (e) => {
    if (e.target.classList.contains('mention')) {
        const contactId = e.target.getAttribute('data-contactid');
        window.location.href = `/notes/${contactId}`;
    }
});