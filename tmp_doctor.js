const db = db.getSiblingDB('kinedical');
db.users.updateOne({ email: 'testuser2@example.com' }, { $set: { role: 'DOCTOR' } });
const user = db.users.findOne({ email: 'testuser2@example.com' }, { username: 1, email: 1, role: 1, status: 1 });
printjson(user);
