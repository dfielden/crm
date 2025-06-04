import {AJAX, isEmptyObject} from "../helper.js";
import {Birthday, Contact} from "../contact.js";

const listAlphabet = document.querySelector(".list-alphabet");

window.addEventListener('load', e => {
    const data = getContacts();
    document.querySelector('#btnUpdateContact').style.display="none";

})

const getContacts = async function() {
    try {
        const data = await AJAX("/getcontacts");
        const dataArr = Object.entries(data);

        dataArr.sort((a, b) => {
            const nameA = (a[1].surname || a[1].firstName || "").toLowerCase();
            const nameB = (b[1].surname || b[1].firstName || "").toLowerCase();
            return nameA.localeCompare(nameB);
        });


        const alphabetArr = [];

        for (const [key, value] of dataArr) {

            const letter =   (value.surname && value.surname.charAt(0)) ||
                (value.firstName && value.firstName.charAt(0)) ||
                "";

            if (!alphabetArr.includes(letter.toUpperCase())) {
                listAlphabet.innerHTML += `<li class="list-alphabet-header">${letter.toUpperCase()}</li>`;
                alphabetArr.push(letter.toUpperCase());
            }

            const img = byteArrayToBase64(new Uint8Array(value.image) ?? '');
            const imgHtml = img === '' ? `<i class="fa-solid fa-user"></i>` : `<img class="profile-img" src="data:image/jpg;base64,${img}">`;

            listAlphabet.innerHTML += `<li class="list-alphabet-item" data-contactid='${value.contactId}'>
                        <div class="img-circle-container">
                            ${imgHtml}
                        </div>
                        <h3>${value.firstName} ${value.surname ?? ''}</h3>
                    </li>`;
        }

        if(isEmptyObject(data)) {
            console.log("no data")
        }

    } catch (err) {
        console.error(err + ': Unable to load contacts. Please try again.');
    }
}

document.querySelector('#btnSubmitNewContact').addEventListener('click', async (e) => {
    e.preventDefault();

    const firstName = document.querySelector('#firstName').value.trim();
    const middleNames = document.querySelector('#middleNames').value.trim();
    const surname = document.querySelector('#surname').value.trim();
    const title = document.querySelector('#title').value.trim();
    const day = document.querySelector('#day').value.trim();
    const month = document.querySelector('#month').value.trim();
    const year = document.querySelector('#year').value.trim();
    const image = document.querySelector('#imageUpload').files[0];

    if (firstName === "") {
        console.log("First name is required");
        return;
    }

    const sendContact = async (b64Img) => {
        const contact = new Contact(
            firstName,
            middleNames,
            surname,
            title,
            new Birthday(day, month, year),
            b64Img || null // ⬅️ Include null if no image
        );

        try {
            const result = await AJAX(`/submitnewcontact`, contact);
            console.log("Contact created:", JSON.parse(result));
        } catch (error) {
            console.error("Upload failed:", error);
        }
    };

    // If there's an image, encode it
    if (image) {
        const reader = new FileReader();
        reader.onload = () => {
            const b64Img = reader.result.split(',')[1];
            sendContact(b64Img); // ✅ Send with image
        };
        reader.readAsDataURL(image);
    } else {
        sendContact(null); // ✅ Send without image
    }
    document.querySelector('#imageUpload').value = '';
});

document.querySelector('#btnUpdateContact').addEventListener('click', async (e) => {
    e.preventDefault();

    const firstName = document.querySelector('#firstName').value.trim();
    const middleNames = document.querySelector('#middleNames').value.trim();
    const surname = document.querySelector('#surname').value.trim();
    const title = document.querySelector('#title').value.trim();
    const day = document.querySelector('#day').value.trim();
    const month = document.querySelector('#month').value.trim();
    const year = document.querySelector('#year').value.trim();
    const image = document.querySelector('#imageUpload').files[0];
    const contactId = document.querySelector('#contactId').value.trim();

    if (firstName === "") {
        console.log("First name is required");
        return;
    }

    const sendContact = async (b64Img) => {
        const contact = new Contact(
            firstName,
            middleNames,
            surname,
            title,
            new Birthday(day, month, year),
            b64Img || null // Include null if no image
        );

        try {
            const result = await AJAX(`/updatecontact/${contactId}`, contact);
            console.log("Contact updated:", JSON.parse(result));
        } catch (error) {
            console.error("Upload failed:", error);
        }
    };

    // If there's an image, encode it
    if (image) {
        const reader = new FileReader();
        reader.onload = () => {
            const b64Img = reader.result.split(',')[1];
            sendContact(b64Img);
        };
        reader.readAsDataURL(image);
    } else {
        await sendContact(null);
    }
    document.querySelector('#imageUpload').value = '';
});

listAlphabet.addEventListener('click', async (e) => {
    const contactId = e.target.closest('.list-alphabet-item').getAttribute('data-contactid');
    const contact = await AJAX(`getcontact/${contactId}`);
    console.log(contact);

    document.querySelector('#btnUpdateContact').style.display="inline-block";
    document.querySelector('#btnSubmitNewContact').style.display="none";

    // Populate form
    document.querySelector('.personal-info-header').textContent = `${contact.firstName}  ${contact.surname ?? ''}`;

    document.querySelector('#firstName').value = contact.firstName;
    document.querySelector('#middleNames').value = contact.middleNames ?? '';
    document.querySelector('#surname').value = contact.surname ?? '';
    document.querySelector('#title').value = contact.title ?? '';
    document.querySelector('#day').value = contact.birthday.day ?? '';
    document.querySelector('#month').value = contact.birthday.month ?? '';
    document.querySelector('#year').value = contact.birthday.year ?? '';
    document.querySelector('#contactId').value = contactId;

    document.querySelector('#imageUpload').value = '';

    const img = byteArrayToBase64(new Uint8Array(contact.image) ?? '');

    document.querySelector('#imgSquareContainer').innerHTML =
        img === '' ? `<i class="fa-solid fa-user"></i>` : `<img class="display-img" src="data:image/jpg;base64,${img}">`;

});

document.querySelector('#newContact').addEventListener('click',  () => {
    document.querySelector('#btnUpdateContact').style.display="none";
    document.querySelector('#btnSubmitNewContact').style.display="inline-block";
    document.querySelector('#contactForm').reset();
    document.querySelector('.personal-info-header').textContent = '';
});

const getJpgFromByteArray = (byteArray) => {
    // Convert byte array to Blob
    const blob = new Blob([byteArray], { type: 'image/jpeg' });

    // Create object URL from Blob
    const imageUrl = URL.createObjectURL(blob);

    // Create and append image
    const img = document.createElement('img');
    img.src = imageUrl;
    img.alt = 'Decoded Image';
    return img;
}

const byteArrayToBase64 = (byteArray) => {
    if (byteArray === '') {
        return;
    }
    let binary = '';
    byteArray.forEach(byte => binary += String.fromCharCode(byte));
    return window.btoa(binary);
}


