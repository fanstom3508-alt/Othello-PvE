# BUSINESS REQUIREMENTS DOCUMENT (BRD)
## Game Othello — Player vs AI (PvE) Desktop Application (Version A+)

---

## 1. Project Overview
Ứng dụng desktop cho phép người dùng chơi Othello (Reversi) với AI mà không cần đăng nhập. Tập trung vào trải nghiệm nhanh, offline và dễ tiếp cận.

---

## 2. Business Objectives
- BO-01: Loại bỏ login → tăng khả năng bắt đầu nhanh
- BO-02: AI đủ thách thức → giữ chân người chơi
- BO-03: Lưu điểm → tạo động lực chơi lại
- BO-04: Offline 100% → tăng tính khả dụng

---

## 3. Value Proposition
- Không cần tài khoản → chơi ngay
- AI thông minh → trải nghiệm cạnh tranh
- Highlight nước đi → hỗ trợ người mới
- Lưu điểm cục bộ → theo dõi tiến bộ

---

## 4. Success Metrics
- Bắt đầu game ≤ 30s
- AI phản hồi ≤ 2s
- 0 lỗi logic trong test
- Không crash trong 20 ván
- Lưu điểm không mất

---

## 5. Scope

### In Scope
- Gameplay Othello đầy đủ
- Player vs AI
- Highlight nước đi
- Lưu điểm local
- Leaderboard top 10
- Replay nhanh

### Out of Scope
- Login / Account
- Multiplayer
- Online server
- Mobile app

---

## 6. Stakeholders
- Sinh viên dev
- Giảng viên
- Người chơi

---

## 7. Business Requirements
- BR-01: Chơi ngay không login
- BR-02: AI luôn có nước hợp lệ
- BR-03: Lưu và xem lại điểm
- BR-04: Offline hoàn toàn
- BR-05: Hiển thị rõ trạng thái game
- BR-06: Chơi lại nhanh

---

## 8. Business Rules
- Đen đi trước
- Phải kẹp quân mới hợp lệ
- Không có nước → skip
- Hết nước → kết thúc
- Nhiều quân hơn → thắng

---

## 9. Functional Requirements

### Onboarding
- FR-01: Nhập tên
- FR-02: Chọn màu

### Gameplay
- FR-03: Khởi tạo bàn cờ
- FR-04: Hiển thị nước hợp lệ
- FR-05: Xử lý nước đi
- FR-06: AI đi tự động
- FR-07: Xử lý skip

### Result
- FR-08: Hiển thị kết quả
- FR-09: Lưu điểm
- FR-10: Leaderboard
- FR-11: Play again

---

## 10. Non-Functional Requirements
- Performance: AI ≤ 2s
- Offline: Không cần mạng
- Reliability: Không mất dữ liệu
- Accuracy: Đúng luật 100%
- Usability: Dễ hiểu

---

## 11. High-Level Scenario
1. Mở app
2. Nhập tên
3. Chọn màu
4. Chơi
5. AI đi
6. Lặp lại
7. Kết thúc
8. Lưu điểm
9. Chơi lại

---

## 12. Constraints & Assumptions

### Constraints
- Không backend
- Không login
- PvE only
- Thời gian 1 học kỳ

### Assumptions
- Người dùng biết dùng máy tính
- Chấp nhận lưu local

---

## 13. Risks
- AI phản hồi chậm nếu chưa tối ưu
- Mất file local → mất điểm
- Người chơi trùng tên → ghi đè dữ liệu

---

## Conclusion
Tài liệu tập trung vào business value, loại bỏ chi tiết dư thừa và đảm bảo đúng chuẩn BRD học thuật.
