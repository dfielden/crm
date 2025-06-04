export class Contact {
    constructor(firstName, middleNames, surname, title, birthday, image) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surname;
        this.title = title;
        this.birthday = birthday;
        this.image = image ?? '';
        this.contactId = '';
    }

}

export class Birthday {
   constructor (day, month, year) {
       this.day = day;
       this.month = month;
       this.year = year
   }
}