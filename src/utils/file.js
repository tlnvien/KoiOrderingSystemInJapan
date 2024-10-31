import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "../config/firebase";

const upLoadFile = async (file) => {
    const storageRef = ref(storage, file.name);
    //lưu file này lên firebase
    const respone = await uploadBytes(storageRef, file);
    //=> lấy cái đường dẫn đến file vừa tạo
    const downloadURL = await getDownloadURL(respone.ref);

    return downloadURL;
}

export default upLoadFile;