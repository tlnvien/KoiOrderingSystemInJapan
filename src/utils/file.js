import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "../config/firebase";

const upLoadFile = async (file) => {
    //lưu file này lên firebase

    //=> lấy cái đường dẫn đến file vừa tạo
    const storageRef = ref(storage, file.name);
    const respone = await uploadBytes(storageRef, file);
    const downloadURL = await getDownloadURL(respone.ref);

    return downloadURL;
}

export default upLoadFile;