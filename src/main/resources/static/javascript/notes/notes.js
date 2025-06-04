import {AJAX, formatTimeMillis, isEmptyObject} from "../helper.js";

window.addEventListener('load', e => {
    const data = getNotes();
})

const getNotes = async function() {
    try {
        const data = await AJAX("/getnotes");
        const dataArr = Object.entries(data);
        dataArr.sort(([, a], [, b]) => parseInt(b.timeStamp) - parseInt(a.timeStamp));

        for (const [key, value] of dataArr) {

            recentNotes.innerHTML += `<div class="recent-notes-tab" data-noteid='${value.uid}'>
                                        <div class="note-content">${JSON.parse(value.noteContent)}</div>
                                            <div class="fa-solid fa-trash"></div>
                                        <div class="time-posted">${formatTimeMillis(value.timeStamp)}</div>
                                    </div>`;
        }
        if(!isEmptyObject(data)) {
            console.log("no data")
        }

    } catch (err) {
        console.error('Unable to load notes. Please try again.');
    }
}

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