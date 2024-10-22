package com.project.KoiBookingSystem.enums;

public enum RequestStatus {
    NOT_TAKEN,
    IN_PROGRESS,
    COMPLETED,
    NULL

    // cái booking request này đã được thg sales nào nhận chưa, nếu chưa thì là not_taken, nếu đang nhận thì là in_progress, còn cái request này đã được nhận,
    // đã tạo luôn tour đó, và khách hàng đã thanh toán luôn thì mới chuyển sang completed, hoặc là tour đã được tạo nhưng bị vướng trường hợp hủy
    // đầu tiên là thg khách hàng nó không chịu thanh toán
    // cái thứ 2 là thg manager k duyệt tour và thg khách hàng không liên hệ được để cập nhật thông tin
    // nếu mà thg khách hàng nó k gặp lại mình, hoặc nó k chịu cái yêu cầu update này thì hủy cái tour đó
}
