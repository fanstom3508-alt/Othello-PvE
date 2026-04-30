# Business Requirements Document (BRD)
## Game Othello — Player vs AI (PvE) Desktop Application

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Business Objectives](#2-business-objectives)
3. [Value Proposition](#3-value-proposition)
4. [Success Metrics & KPI](#4-success-metrics--kpi)
5. [Project Scope](#5-project-scope)
6. [Stakeholders & Actors](#6-stakeholders--actors)
7. [Business Requirements](#7-business-requirements)
8. [Business Rules](#8-business-rules)
9. [Functional Requirements](#9-functional-requirements)
10. [Non-Functional Requirements](#10-non-functional-requirements)
11. [High-Level Scenario](#11-high-level-scenario)
12. [Constraints & Assumptions](#12-constraints--assumptions)
13. [Risks](#13-risks)
14. [Future Enhancements](#14-future-enhancements)

---

## 1. Project Overview

### 1.1 Mô tả

Dự án xây dựng một ứng dụng desktop cho phép người dùng chơi trò chơi cờ lật **Othello** (còn gọi là Reversi) theo chế độ **một người chơi đấu với máy tính (Player vs AI — PvE)**.

Người chơi không cần tạo tài khoản hay đăng nhập. **Phiên bản MVP hiện tại** tập trung vào core gameplay chính xác và ổn định. Các tính năng như nhập tên hiển thị, chọn màu quân, AI thực chiến, và hệ thống điểm cao sẽ được phát triển ở các phiên bản tiếp theo (xem [mục 14 — Future Enhancements](#14-future-enhancements)).

### 1.2 Bối cảnh & Vấn đề cần giải quyết

Sinh viên và người yêu thích trò chơi trí tuệ thường không có bạn chơi cùng, nhưng các giải pháp hiện tại (web game Othello, ứng dụng mobile) đều yêu cầu đăng ký tài khoản hoặc kết nối Internet — tạo ra rào cản không cần thiết cho người muốn chơi nhanh, đơn giản.

> *"Tôi muốn có một ứng dụng Othello mà tôi có thể mở ra và chơi ngay với máy tính mà không cần phải đăng ký hay đăng nhập."*
> — Người dùng đại diện (sinh viên đại học)

---

## 2. Business Objectives

| ID | Mục tiêu kinh doanh | Lý do tồn tại |
|---|---|---|
| **BO-01** | Loại bỏ hoàn toàn rào cản đăng ký/đăng nhập | Người dùng bỏ ứng dụng ngay ở bước xác thực — giảm friction = tăng tỷ lệ sử dụng |
| **BO-02** | Cung cấp đối thủ AI đủ thách thức để thay thế bạn chơi | Người dùng không có bạn chơi cùng nhưng vẫn muốn trải nghiệm cạnh tranh có chiều sâu *(phát triển ở phiên bản sau)* |
| **BO-03** | Tạo động lực chơi lại thông qua bảng điểm cá nhân | Không có vòng lặp động lực → người dùng chơi một lần rồi bỏ *(phát triển ở phiên bản sau)* |
| **BO-04** | Ứng dụng hoạt động hoàn toàn offline, không phụ thuộc server | Đảm bảo tính khả dụng 100% bất kể kết nối mạng |

---

## 3. Value Proposition

| Đối tượng | Vấn đề hiện tại | Giá trị mà ứng dụng mang lại |
|---|---|---|
| Sinh viên yêu thích game trí tuệ | Không có bạn chơi, các web game yêu cầu đăng ký | Chơi ngay trong < 30 giây, không cần tài khoản |
| Người mới học Othello | Khó nhớ luật — không biết đặt quân ở đâu | Highlight nước đi hợp lệ ngay trên bàn cờ |
| Người muốn tự cải thiện | Không có cách theo dõi tiến bộ khi không có tài khoản | Bảng điểm cục bộ lưu kỷ lục cá nhân giữa các phiên |

---

## 4. Success Metrics & KPI

| ID | Chỉ số | Mục tiêu | Cách đo |
|---|---|---|---|
| **KPI-01** | Thời gian từ mở ứng dụng đến nước đi đầu tiên | ≤ 30 giây | Đo thủ công khi demo |
| **KPI-02** | Độ chính xác logic game | 0 lỗi trong 20 test case chuẩn theo luật Othello | Kiểm thử bằng bộ test case được thiết kế sẵn |
| **KPI-03** | Tỷ lệ ván đấu hoàn thành (không crash) | 100% trong 20 ván test liên tiếp | Chạy 20 ván, đếm số ván kết thúc bình thường |

---

## 5. Project Scope

### 5.1 In Scope *(Phiên bản MVP)*

1. Bàn cờ Othello 8×8 với logic game đầy đủ theo luật chuẩn quốc tế.
2. Highlight nước đi hợp lệ cho người chơi trong mỗi lượt.
3. Hiển thị trực quan quân bị lật sau mỗi nước đi.
4. Xử lý tự động trường hợp bỏ lượt khi không có nước đi hợp lệ.
5. Màn hình kết quả sau mỗi ván đấu (thắng / thua / hòa).
6. Tính năng chơi lại nhanh.

> **Lưu ý:** Các tính năng nhập tên, chọn màu quân, AI thực chiến, lưu điểm cao, và bảng xếp hạng được liệt kê tại [mục 14 — Future Enhancements](#14-future-enhancements).

### 5.2 Out of Scope

1. Hệ thống đăng ký / đăng nhập / xác thực tài khoản.
2. Chế độ nhiều người chơi (PvP — Player vs Player).
3. Tích hợp server hoặc cơ sở dữ liệu từ xa.
4. Leaderboard trực tuyến hoặc đồng bộ giữa các thiết bị.
5. Chức năng quản trị (Admin panel).
6. Ứng dụng di động (mobile/tablet).
7. Tính năng chỉnh độ khó AI (reserved for future version).

---

## 6. Stakeholders & Actors

### 6.1 Stakeholders

| Stakeholder | Vai trò | Kỳ vọng chính |
|---|---|---|
| Sinh viên phát triển | Thiết kế, lập trình, kiểm thử | Hoàn thành trong một học kỳ, đủ điểm môn |
| Giảng viên hướng dẫn | Định hướng, nghiệm thu | Tài liệu đúng chuẩn, sản phẩm demo được đầy đủ flow |
| Người dùng cuối | Sử dụng ứng dụng | Chơi được ngay, không rào cản, AI đủ thách thức |

### 6.2 Actors trong hệ thống

| Actor | Loại | Vai trò | Tương tác chính |
|---|---|---|---|
| **Player** | Con người | Người chơi duy nhất, thực hiện các thao tác game | Đặt quân, xem kết quả, chơi lại |
| **System** | Phần mềm | Xử lý logic game, điều phối luồng | Khởi tạo game, xử lý nước đi, phát hiện kết thúc |

> **Ghi chú:** AI Opponent chưa được tích hợp trong phiên bản MVP. Tính năng AI thực chiến sẽ được thêm vào ở phiên bản tiếp theo (xem FE-03).

---

## 7. Business Requirements

Đây là các yêu cầu ở cấp độ nghiệp vụ — mô tả *điều gì cần xảy ra* từ góc nhìn người dùng và mục tiêu kinh doanh, không phụ thuộc vào giải pháp kỹ thuật cụ thể.

| ID | Business Requirement | Liên quan đến BO |
|---|---|---|
| **BR-01** | Người dùng phải có thể bắt đầu một ván đấu hoàn chỉnh mà không cần thực hiện bất kỳ bước đăng ký hay xác thực nào. | BO-01 |
| **BR-02** | Hệ thống phải cung cấp bàn cờ Othello đúng luật, xử lý chính xác mọi nước đi và trạng thái bàn cờ. | BO-02 |
| **BR-03** | Ứng dụng phải hoạt động đầy đủ chức năng mà không cần kết nối Internet tại bất kỳ thời điểm nào. | BO-04 |
| **BR-04** | Người dùng phải được thông báo rõ ràng về mọi sự kiện quan trọng trong ván đấu (lượt bị bỏ, kết thúc trận). | BO-02 |
| **BR-05** | Hệ thống phải cho phép người chơi bắt đầu ván mới ngay lập tức sau khi kết thúc ván trước. | BO-01 |

---

## 8. Business Rules

Các quy tắc bất biến xuất phát từ luật chơi Othello chuẩn quốc tế và các ràng buộc nghiệp vụ của hệ thống.

| ID | Business Rule |
|---|---|
| **BRule-01** | Quân đen luôn đi trước trong mọi ván đấu, không có ngoại lệ. |
| **BRule-02** | Một nước đi chỉ hợp lệ nếu quân được đặt xuống kẹp được ít nhất một quân đối thủ theo hàng ngang, hàng dọc, hoặc đường chéo. |
| **BRule-03** | Tất cả quân đối thủ bị kẹp bởi một nước đi đều phải bị lật cùng một lúc, không có ngoại lệ. |
| **BRule-04** | Nếu một bên không có nước đi hợp lệ, lượt đó tự động bị bỏ qua và quyền đi chuyển sang bên còn lại. |
| **BRule-05** | Ván đấu kết thúc khi cả hai bên đều không còn nước đi hợp lệ hoặc bàn cờ đã đầy. |
| **BRule-06** | Bên có nhiều quân trên bàn cờ khi ván kết thúc là bên thắng. Nếu bằng nhau, kết quả là hòa. |
| **BRule-07** | Điểm số của người chơi là số quân của họ trên bàn cờ khi ván kết thúc (tối đa 64). |

> **Ghi chú:** Các quy tắc liên quan đến tên người chơi (BRule-08) và bảng xếp hạng (BRule-09) sẽ được bổ sung khi tính năng tương ứng được phát triển (xem FE-01, FE-04).

---

## 9. Functional Requirements

Mỗi yêu cầu chức năng mô tả *điều gì* hệ thống cần thực hiện. Chi tiết kỹ thuật và acceptance criteria chi tiết thuộc phạm vi SRS.

### 9.1 Gameplay

| ID | Mô tả | Liên quan đến BR |
|---|---|---|
| **FR-01** | Hệ thống tự động khởi tạo bàn cờ 8×8 với thế quân ban đầu chuẩn khi ván đấu bắt đầu. | BR-02 |
| **FR-02** | Hệ thống tính toán và hiển thị tất cả nước đi hợp lệ cho người chơi tại đầu mỗi lượt. | BR-02, BR-04 |
| **FR-03** | Hệ thống xử lý nước đi của người chơi, lật tất cả quân bị kẹp và cập nhật trạng thái bàn cờ ngay lập tức. | BR-02 |
| **FR-04** | Hệ thống tự động phát hiện và xử lý trường hợp một bên không có nước đi hợp lệ, thông báo cho người chơi và chuyển lượt. | BR-04 |

### 9.2 Result

| ID | Mô tả | Liên quan đến BR |
|---|---|---|
| **FR-05** | Hệ thống phát hiện điều kiện kết thúc ván đấu, đếm quân và hiển thị kết quả thắng/thua/hòa. | BR-04 |
| **FR-06** | Hệ thống cho phép người chơi bắt đầu ván mới ngay lập tức từ màn hình kết quả. | BR-05 |

---

## 10. Non-Functional Requirements

| ID | Danh mục | Mô tả | Mức ưu tiên |
|---|---|---|---|
| **NFR-01** | Hiệu năng | Bàn cờ cập nhật sau mỗi nước đi trong < 200ms. | High |
| **NFR-02** | Tính khả dụng | Ứng dụng hoạt động hoàn toàn offline, không yêu cầu kết nối Internet tại bất kỳ thời điểm nào. | High |
| **NFR-03** | Độ chính xác | Logic game tuân thủ chính xác 100% luật Othello tiêu chuẩn quốc tế trong mọi tình huống. | High |
| **NFR-04** | Tính sử dụng | Giao diện đủ trực quan để người dùng mới hoàn thành ván đấu đầu tiên mà không cần hướng dẫn bên ngoài. | Medium |
| **NFR-05** | Khả năng mở rộng | Kiến trúc hệ thống cho phép bổ sung AI, hệ thống điểm cao, chọn màu quân hoặc chế độ PvP trong phiên bản tương lai mà không cần thiết kế lại toàn bộ. | Low |

---

## 11. High-Level Scenario

Luồng hoạt động tổng thể của phiên bản MVP từ khi người chơi mở ứng dụng đến khi kết thúc một phiên chơi:

| # | Actor | Bước | Mô tả |
|---|---|---|---|
| 1 | Player | Mở ứng dụng | Player truy cập ứng dụng, thấy màn hình chào. |
| 2 | System | Khởi tạo trận đấu | System thiết lập bàn cờ 8×8, đặt 4 quân ban đầu, xác định lượt đi đầu tiên (đen đi trước). |
| 3 | System | Hiển thị nước đi hợp lệ | System tính toán và highlight các ô hợp lệ của Player. |
| 4 | Player | Thực hiện nước đi | Player chọn một ô hợp lệ để đặt quân. |
| 5 | System | Xử lý nước đi của Player | System lật quân bị kẹp, cập nhật bàn cờ và điểm số. |
| 6 | System | Kiểm tra lượt bị bỏ qua | System kiểm tra có nước đi hợp lệ không; nếu không thì bỏ qua và thông báo. |
| 7 | System | Phát hiện kết thúc trận | System phát hiện không còn nước đi hợp lệ cho cả hai bên. |
| 8 | System | Hiển thị kết quả | System đếm quân, công bố thắng/thua/hòa. |
| 9 | Player | Xem kết quả / Chơi lại | Player xem kết quả hoặc bắt đầu ván mới. |

> **Luồng ngoại lệ:** Bước 6 có thể xảy ra sau bất kỳ bước 5 nào. Nếu cả hai bên đều không có nước đi hợp lệ liên tiếp, hệ thống nhảy thẳng đến bước 7.

---

## 12. Constraints & Assumptions

### 12.1 Constraints

| Ràng buộc | Mô tả |
|---|---|
| Không có backend server | Toàn bộ dữ liệu được xử lý và lưu trữ cục bộ trên máy người dùng. |
| Không có xác thực người dùng | Danh tính người chơi chỉ là tên hiển thị nhập trước ván đấu, không có cơ chế xác minh. |
| Chỉ hỗ trợ chế độ PvE | Không có chức năng nhiều người chơi trong phạm vi dự án này. |
| Phạm vi học kỳ | Dự án phải hoàn thành trong một học kỳ với đội một người. |
| Không có tính năng undo | Người chơi không thể rút lại nước đi đã thực hiện. |

### 12.2 Assumptions

- Người dùng đã quen với cách sử dụng ứng dụng desktop cơ bản (click chuột, nhập văn bản).
- Người dùng chấp nhận bảng điểm cao chỉ lưu trên thiết bị cục bộ, không đồng bộ giữa các máy.
- Người dùng hiểu cơ bản khái niệm thắng/thua trong game theo số quân — không cần giải thích thêm trên màn hình kết quả.
- Máy tính của người dùng đủ tài nguyên để chạy ứng dụng desktop thông thường.
- Một ô trên bàn cờ chỉ có thể chứa một quân duy nhất tại một thời điểm.
- Logic game tuân thủ hoàn toàn luật Othello/Reversi tiêu chuẩn quốc tế (World Othello Federation rules).

---

## 13. Risks

| ID | Rủi ro | Khả năng xảy ra | Mức độ ảnh hưởng | Biện pháp giảm thiểu |
|---|---|---|---|---|
| **R-01** | Logic game tính sai nước đi trong các edge case (bàn cờ gần đầy, không có nước đi) | Cao | Cao — ảnh hưởng trực tiếp đến tính đúng đắn của game | Viết test case cho toàn bộ edge case của luật Othello trước khi tích hợp thêm tính năng |
| **R-02** | Scope bị mở rộng ngoài kế hoạch, ảnh hưởng tiến độ nộp bài | Trung bình | Cao — ảnh hưởng đến toàn bộ tiến độ | Giới hạn MVP ở FR-01 đến FR-06; các tính năng trong Future Enhancements chỉ phát triển khi core ổn định |
| **R-03** | Người dùng bị confuse khi không có màn hình nhập tên trong phiên bản cơ bản | Thấp | Thấp — trải nghiệm hơi lạ nhưng không ảnh hưởng gameplay | Thông báo rõ trong UI rằng chức năng nhập tên sẽ có trong phiên bản tiếp theo |

---

## 14. Future Enhancements

Các tính năng sau đây là **yêu cầu của khách hàng** nhưng chưa được đưa vào phiên bản MVP. Mục tiêu trước mắt là hoàn thiện và ổn định core gameplay trước khi phát triển thêm. Các tính năng này sẽ được xem xét và tích hợp trong các phiên bản tiếp theo.

| ID | Tính năng | Mô tả | Liên quan đến FR/BR gốc |
|---|---|---|---|
| **FE-01** | Nhập tên người chơi | Cho phép người chơi nhập tên hiển thị trước khi bắt đầu ván đấu. Tên được sử dụng để gán điểm vào bảng xếp hạng và hiển thị trong màn hình kết quả. Tên phải chứa ít nhất 1 ký tự, không xác thực tính duy nhất. | FR-01 (cũ), BRule-08 |
| **FE-02** | Chọn màu quân (đen / trắng) | Cho phép người chơi lựa chọn chơi quân đen hoặc quân trắng trước mỗi ván đấu. Nếu chọn trắng, bên còn lại sẽ đi trước theo đúng luật (đen luôn đi trước). | FR-02 (cũ), BRule-01 |
| **FE-03** | AI tự động thực hiện nước đi | Tích hợp thuật toán AI (Minimax / Alpha-Beta Pruning) để AI tự tính toán và thực hiện nước đi sau mỗi lượt của người chơi. AI phản hồi trong ≤ 2 giây. Hiển thị rõ vị trí AI vừa đặt quân. | FR-06 (cũ), BR-02, NFR-01 |
| **FE-04** | Lưu điểm cao (Save High Score) | Tự động lưu điểm số người chơi vào file cục bộ sau mỗi ván hoàn thành. Nếu cùng tên đã tồn tại, chỉ cập nhật nếu điểm mới cao hơn. Dữ liệu bền vững qua các phiên. | FR-09 (cũ), BR-03, BRule-09 |
| **FE-05** | Xem bảng xếp hạng (High Score Leaderboard) | Hiển thị Top 10 điểm cao được lưu cục bộ, có thể truy cập từ màn hình chào hoặc màn hình kết quả. Danh sách sắp xếp từ cao đến thấp. | FR-10 (cũ), BO-03 |

> **Thứ tự ưu tiên phát triển đề xuất:** FE-01 → FE-02 → FE-03 → FE-04 → FE-05.
> FE-04 và FE-05 phụ thuộc vào FE-01 (cần có tên người chơi để gán điểm), nên cần hoàn thành FE-01 trước.

---

*Phiên bản 2.1 — Trần Lê Minh Mẫn — Tháng 4/2026 — Đại học Nông Lâm TP.HCM*
