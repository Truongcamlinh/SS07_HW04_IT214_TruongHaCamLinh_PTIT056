# SS07 HW04: Chuyển đổi từ RestTemplate sang FeignClient


## Mục tiêu

Bài này refactor luồng chuyển tiền của FinBank từ `RestTemplate` sang Spring Cloud OpenFeign. `transaction-service` không tự ghép URL và gọi HTTP thủ công nữa, mà khai báo interface `AccountServiceClient` và `CustomerServiceClient`.

## Module

```text
discovery-server    : Eureka Server, port 8761
api-gateway         : Gateway, port 8222
account-service     : Quản lý tài khoản, port 8081
transaction-service : Xử lý chuyển tiền, port 8082
customer-service    : Quản lý khách hàng, port 8083
```

## API Account Service

```text
GET /api/accounts/{accountNumber}
GET /api/accounts/{accountNumber}/balance
PUT /api/accounts/{accountNumber}/debit
PUT /api/accounts/{accountNumber}/credit
```

Ví dụ body cho debit/credit:

```json
{
  "amount": 2000000
}
```

## API Transaction Service

```text
POST /api/transactions/transfer
GET /api/transactions/{id}/detail
```

Gọi qua Gateway:

```text
POST http://localhost:8222/api/transactions/transfer
```

Body:

```json
{
  "fromAccountNumber": "1001",
  "toAccountNumber": "1002",
  "amount": 2000000,
  "description": "Chuyen tien thanh toan hoa don"
}
```

## Dữ liệu mẫu

Khi `account-service` và `customer-service` khởi động, hệ thống tự tạo:

```text
1001 - 10,000,000 VND
1002 - 5,000,000 VND
1001 - Nguyen Van A
1002 - Tran Thi B
```

## Thứ tự chạy

```bash
./gradlew :discovery-server:bootRun
./gradlew :account-service:bootRun
./gradlew :customer-service:bootRun
./gradlew :transaction-service:bootRun
./gradlew :api-gateway:bootRun
```

## Test case Postman

Collection nằm trong thư mục:

```text
postman/FinBank_SS07_HW04.postman_collection.json
```

Các case chính:

- Chuyển `2,000,000` từ `1001` sang `1002`: `SUCCESS`
- Chuyển `100,000,000` từ `1001` sang `1002`: `FAILED` vì không đủ số dư
- Chuyển từ `1001` sang `9999`: `FAILED` vì tài khoản đích không tồn tại
- Xem chi tiết giao dịch: `GET http://localhost:8222/api/transactions/1/detail`

## FeignClient trong Transaction Service

`TransactionServiceApplication` bật Feign:

```java
@EnableFeignClients
```

Client gọi Account Service:

```java
@FeignClient(name = "account-service")
public interface AccountServiceClient {
    @GetMapping("/api/accounts/{accountNumber}")
    AccountResponse getAccount(@PathVariable String accountNumber);
}
```

Client gọi Customer Service:

```java
@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/customers/by-account/{accountNumber}")
    CustomerResponse getByAccountNumber(@PathVariable String accountNumber);
}
```

## So sánh RestTemplate vs FeignClient

Sau khi làm cả hai cách, FeignClient gọn hơn RestTemplate vì không phải tự nối URL, tự truyền biến path bằng chuỗi và tự parse response thủ công. Code đọc giống gọi hàm Java bình thường nên `TransferService` tập trung vào nghiệp vụ chuyển tiền hơn là chi tiết HTTP. RestTemplate linh hoạt và dễ hiểu lúc mới học, nhưng khi nhiều endpoint tăng lên thì code client dài và lặp lại nhiều. FeignClient phù hợp hơn cho microservice có nhiều lời gọi nội bộ vì contract được gom vào interface rõ ràng, dễ bảo trì hơn.
