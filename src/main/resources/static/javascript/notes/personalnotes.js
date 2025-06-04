import {AJAX, formatTimeMillis, isEmptyObject} from "../helper.js";
const personalNotes = document.querySelector('#personalNotes');

window.addEventListener('load', e => {
    const notes = getNotes();
    const contact = getContact();
})


const getNotes = async function(id) {
    try {
        const url = window.location.href;
        const contactId = url.substring(url.lastIndexOf('/') + 1);
        const data = await AJAX(`/getnotes/${contactId}`);
        console.log(data);
        const dataArr = Object.entries(data);
        dataArr.sort(([, a], [, b]) => parseInt(b.timeStamp) - parseInt(a.timeStamp));

        for (const [key, value] of dataArr) {

            personalNotes.innerHTML += `<div class="recent-notes-tab" data-noteid='${value.uid}'>
                                        <div class="note-content">${JSON.parse(value.noteContent)}</div>
                                            <div class="fa-solid fa-trash"></div>
                                        <div class="time-posted">${formatTimeMillis(value.timeStamp)}</div>
                                    </div>`;
        }
        if(isEmptyObject(data)) {
            console.log("no notes data")
        }

    } catch (err) {
        console.error('Unable to load notes. Please try again.');
    }
}

const getContact = async () => {
    try {
        const url = window.location.href;
        const contactId = url.substring(url.lastIndexOf('/') + 1);
        const data = await AJAX(`/getcontact/${contactId}`);
        
        document.querySelector('#contactNotesName').textContent = `${data.firstName} ${data.surname}`;

        if(isEmptyObject(data)) {
            console.log("no contact data")
        }

    } catch (err) {
        console.error('Unable to load contact. Please try again.');
    }
}

document.querySelector('#personalNotes').addEventListener('click', async (e) => {
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