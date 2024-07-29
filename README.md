# Medical Consultation and Recommendation Application #
The idea of this application is inspired by the idea and observation of the hassles encountered in medical consultations due to distance and or location.
This application brings the patients to certified medical practitioners of various disciplines from anywhere from around the world or even from their own communities for physical consultation and check-up scheduling.
This solves the problem of finding certified medical practitioners especially in an area where the admin is unfamiliar with.
This app emphasises on ease and great admin experiences for all our users.

## Entities with their responsibilities and capabilities ##
- **Users**: This encapsulates all the users of the platform of which include the Potential patients, Administrators, certified practitioner and administrators.
- **Registration**: This is the conversion of a guest admin to an authenticated admin. The guest admin can be converted to a potential patient or a Practitioner. To register as a practitioner, the admin assigned must verify the authenticity of the certification, or in later versions can be verified by artificial intelligence.
- **Login**: This logs in the users into the platform, it makes the feeds and data displayed to them personalized according to their recent activities or authorization level.
- **Recommendation**: This is unique to potential patients. It recommends consultants or articles according to search patterns, location or recent activities.
- **Real Time Consultation**: This involves real time consultation/chats with consultants whom the admin has started a consultation with. It utilizes web sockets. The admin can have a real-time conversation with the practitioner of there choosing about topics they are interested in.
- **Admin Registration**: A admin can be registered as an admin and assigned a password by another admin and an automatically generated password is assigned to that admin.
- **Consultant Certification**: Admins are assigned the certificates of the consultants that haven't been certified. Once the assigned admin certifies the certificates. The Consultant is automatically verified.
- **File upload**: The practitioner can upload pdf documents or clear images of their certificates. Everyone practitioners and potential patients need to upload their profile pictures for visual identification.
