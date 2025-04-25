import {AJAX} from "../helper.js";

// TODO: get user info from DB
const users = ["alice", "bob", "charlie", "diana", "eve", "worm"];
const newNote = document.querySelector("#newNote");
const suggestions = document.querySelector("#mentionSuggestions");


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
        const matchedUsers = users.filter(u => u.startsWith(query));

        if (matchedUsers.length) {
            const rect = range.getBoundingClientRect();
            suggestions.style.top = rect.bottom + window.scrollY + "px";
            suggestions.style.left = rect.left + window.scrollX + "px";
            suggestions.innerHTML = matchedUsers.map(name => `<div>${name}</div>`).join("");
            suggestions.style.display = "block";

            Array.from(suggestions.children).forEach(child => {
                child.onclick = () => {
                    const fullMention = "@" + child.textContent + " ";
                    container.textContent = beforeCursor.slice(0, match.index) +
                        fullMention +
                        text.slice(offset);

                    suggestions.style.display = "none";
                    newNote.focus();

                    // set caret pos to end
                    setCaretPosition(newNote, newNote.textContent.length)

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
    return text.replace(/@(\w+)/g, '<span class="mention">@$1</span>');
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
    newNote.innerHTML = highlightMentions(plainText);

    // Restore caret
    setCaretPosition(newNote, caretOffset);
});

document.querySelector('#btn-submit-new-note').addEventListener('click', (e) => {
    AJAX('/submitnewnote', ' ')
})