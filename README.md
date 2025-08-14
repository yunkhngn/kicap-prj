# Kicap Project

## Giới thiệu
Dự án Kicap được xây dựng bằng Java theo mô hình MVC, sử dụng Maven để quản lý thư viện và HikariCP để kết nối cơ sở dữ liệu MSSQL.

## Cấu trúc thư mục
```
controller       - Chứa các lớp Controller
dao              - Chứa các lớp truy cập dữ liệu
db               - Chứa các lớp cấu hình kết nối cơ sở dữ liệu
filter           - Chứa các lớp filter cho ứng dụng
model            - Chứa các lớp mô hình dữ liệu
service          - Chứa các lớp xử lý nghiệp vụ
```

## Yêu cầu hệ thống
- JDK 17
- Maven
- MSSQL Server
- IDE hỗ trợ Maven (NetBeans, IntelliJ IDEA, Eclipse)
- Sử dụng NetBeans để phát triển

## Cách chạy dự án
1. Mở dự án bằng NetBeans.
2. Cấu hình database trong DAO (file `DBConfig`).
3. Chạy project trên server hỗ trợ Java Web (Tomcat) trong NetBeans.

## Liên hệ
- Tác giả: Khoa Nguyễn
- Email: rogkhoa.mail@gmail.com
# Kicap Project

## Giới thiệu
Kicap Project là một website bán bàn phím cơ, được phát triển bằng Java Servlet & JSP theo mô hình MVC, sử dụng Maven để quản lý thư viện và JDBC để kết nối cơ sở dữ liệu MSSQL.  
Website có 3 nhóm trang chính:
- **Guest**: Xem sản phẩm, tìm kiếm, xem chi tiết sản phẩm, đăng ký, đăng nhập, thêm vào giỏ hàng.
- **User**: Các chức năng như Guest + quản lý giỏ hàng, đặt hàng, xem lịch sử mua hàng, cập nhật thông tin cá nhân.
- **Admin**: Quản lý sản phẩm, danh mục, đơn hàng, tài khoản; gán quyền admin cho user khác.

## Chức năng chi tiết
### Guest
- Xem danh sách sản phẩm theo danh mục.
- Xem chi tiết sản phẩm.
- Tìm kiếm sản phẩm theo tên.
- Đăng ký tài khoản mới.
- Đăng nhập hệ thống.
- Thêm sản phẩm vào giỏ hàng (session-based).

### User
- Tất cả chức năng Guest.
- Cập nhật thông tin cá nhân.
- Quản lý giỏ hàng (thêm, xóa, cập nhật số lượng).
- Đặt hàng.
- Xem lịch sử mua hàng.

### Admin
- Quản lý sản phẩm (thêm, sửa, xóa, tìm kiếm).
- Quản lý danh mục sản phẩm.
- Quản lý đơn hàng (xem, cập nhật trạng thái).
- Quản lý tài khoản (cấp quyền admin, khóa/mở tài khoản).

## Cấu trúc thư mục (theo mô hình MVC)
```
src/
  main/
    java/
      controller/   - Chứa các servlet điều khiển luồng xử lý
      dao/          - Chứa các lớp truy xuất dữ liệu
      db/           - Cấu hình kết nối cơ sở dữ liệu (DBContext, DBConfig)
      filter/       - Chứa các filter xử lý request/response
      model/        - Các lớp mô hình (Product, User, Order, ...)
      service/      - Các lớp xử lý nghiệp vụ
    webapp/
      WEB-INF/
        views/      - JSP hiển thị giao diện
      assets/       - CSS, JS, images
```

## Yêu cầu hệ thống
- JDK 17
- Apache Maven
- MSSQL Server
- Apache Tomcat 9+
- IDE hỗ trợ Maven (NetBeans khuyến nghị)

## Cài đặt & chạy dự án
1. Clone repository:
   ```bash
   git clone https://github.com/yunkhngn/kicap-prj.git
   ```
2. Mở dự án trong NetBeans.
3. Cấu hình cơ sở dữ liệu:
   - Tạo database MSSQL bằng file `kicap.sql` (bao gồm cả dữ liệu mẫu).
   - Sửa thông tin kết nối trong `db/DBConfig.java` (URL, username, password).
4. Chạy dự án trên Apache Tomcat trong NetBeans.
5. Truy cập trình duyệt:
   - Trang chủ: `http://localhost:8080/kicap`
   - Trang admin: `http://localhost:8080/kicap/admin`

## Liên hệ
- Tác giả: Khoa Nguyễn
- Email: rogkhoa.mail@gmail.com