# مدیریت حساب بانکی (Account Balance Calculator)

این یک پروژه جاوا است که یک محاسبه‌گر موجودی حساب با مدیریت تاریخچه تراکنش‌ها را پیاده‌سازی می‌کند.

## ساختار پروژه

- `src/main/java/edu/sharif/selab/`
  - `AccountBalanceCalculator.java` - کلاس اصلی محاسبه‌گر
  - `Transaction.java` - مدل تراکنش
  - `TransactionType.java` - enum نوع تراکنش (DEPOSIT/WITHDRAWAL)
  - `Main.java` - نقطه ورود برنامه
- `src/test/java/edu/sharif/selab/`
  - `AccountBalanceCalculatorTest.java` - تست‌های واحد

## نحوه اجرای تست‌ها

برای اجرای تمام تست‌ها، دستور زیر را در دایرکتوری اصلی پروژه اجرا کنید:

```bash
mvn test
```

این دستور پروژه را کامپایل کرده و تمام 9 تست واحد را اجرا می‌کند.

## اصلاحات اخیر

### مشکل مدیریت تاریخچه تراکنش‌ها

**مشکل:** متد `calculateBalance()` به درستی تاریخچه تراکنش‌ها را مدیریت نمی‌کرد. تست‌ها شکست می‌خوردند زیرا:

- تاریخچه تراکنش‌ها همیشه خالی بود (0 تراکنش) در حالی که تست‌ها انتظار 2-3 تراکنش داشتند
- متد موجودی را درست محاسبه می‌کرد اما تراکنش‌ها را در تاریخچه ذخیره نمی‌کرد

**راه‌حل:** متد `calculateBalance()` در `AccountBalanceCalculator.java` به شکل زیر تغییر یافت:

1. پاک کردن تاریخچه تراکنش‌های قبلی: `transactionHistory.clear()`
2. اضافه کردن تراکنش‌های جدید به تاریخچه: `transactionHistory.addAll(transactions)`

**خطوط تغییر یافته:** خطوط 12-14 در `AccountBalanceCalculator.java`

```java
// پاک کردن تاریخچه تراکنش‌های قبلی و اضافه کردن تراکنش‌های جدید
transactionHistory.clear();
transactionHistory.addAll(transactions);
```

این اطمینان می‌دهد که:

- ✅ تاریخچه تراکنش‌ها شامل تعداد صحیح تراکنش‌ها پس از محاسبات است
- ✅ هر فراخوانی `calculateBalance()` تاریخچه قبلی را با تراکنش‌های جدید جایگزین می‌کند
- ✅ تاریخچه تراکنش‌ها از طریق `getTransactionHistory()` قابل دسترسی است

## خطاهای بحرانی کشف شده و رفع شده

### خطاهای مخفی در کلاس تراکنش ⚠️

در حالی که 9 تست اولیه پاس می‌شدند، خطاهای بحرانی در کلاس `Transaction` کشف شد که توسط مجموعه تست اصلی پوشش داده نشده بودند:

#### 🐛 خطای شماره ۱: پذیرش مقادیر منفی

**مشکل:** سازنده تراکنش مقادیر منفی را بدون validation می‌پذیرفت

- `new Transaction(DEPOSIT, -100)` مجاز بود
- `new Transaction(WITHDRAWAL, -50)` باعث محاسبه نادرست موجودی می‌شد (جمع به جای تفریق)

**تأثیر:** یک برداشت با مقدار منفی موجودی را افزایش می‌داد به جای کاهش آن!

**اصلاح:** اضافه کردن validation در سازنده:

```java
if (amount < 0) {
    throw new IllegalArgumentException("Transaction amount cannot be negative");
}
```

#### 🐛 خطای شماره ۲: پذیرش نوع تراکنش null

**مشکل:** سازنده نوع `TransactionType` null را می‌پذیرفت

- `new Transaction(null, 100)` مجاز بود
- می‌توانست باعث `NullPointerException` در حین محاسبه شود

**اصلاح:** اضافه کردن بررسی null:

```java
if (type == null) {
    throw new IllegalArgumentException("Transaction type cannot be null");
}
```

#### 🐛 خطای شماره ۳: نبود equals() و hashCode()

**مشکل:** اشیاء تراکنش با مقادیر یکسان برابر در نظر گرفته نمی‌شدند

- باعث مشکلات در عملیات Collections می‌شد
- تست‌های `containsAll()` در تاریخچه تراکنش‌ها به صورت تصادفی کار می‌کردند

**اصلاح:** پیاده‌سازی متدهای صحیح `equals()` و `hashCode()`:

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Transaction that = (Transaction) obj;
    return amount == that.amount && type == that.type;
}
```

### رویکرد تست‌نویسی

- اضافه کردن تست‌های جامع edge case برای آشکار کردن این خطاها
- تمام خطاها شناسایی و با validation مناسب رفع شدند
- کلاس تراکنش حالا از اصول برنامه‌نویسی دفاعی پیروی می‌کند

## نتایج تست (پس از رفع خطاها)

- **کل تست‌ها:** 13 (4 تست جدید اضافه شد)
- **موفق:** 13 ✅
- **ناموفق:** 0
- **وضعیت:** تمام تست‌ها با validation محکم پاس می‌شوند

### درس آموخته شده

✅ **همیشه edge case ها و ورودی‌های نامعتبر را تست کنید**  
❌ پاس شدن تست‌های پایه به معنای بدون خطا بودن کد نیست

## سوالات تحلیلی و پاسخ‌ها

### سوال اول - خطای موجود و علت دیده نشدن آن

**خطا:** کلاس `Transaction` مقادیر منفی و `null` را بدون validation می‌پذیرفت و `equals()/hashCode()` پیاده‌سازی نشده بود.

**علت دیده نشدن:** تست‌های اولیه فقط روی happy path متمرکز بودند و edge cases و invalid inputs را بررسی نمی‌کردند، بنابراین خطاهای validation و object comparison کشف نشدند.

### سوال دوم - تست برای کشف خطا و اصلاح آن

**تست‌های نوشته شده برای کشف خطا:**

```java
@Test
void testNegativeAmountShouldThrowException() {
    // کشف خطای پذیرش مقادیر منفی
    assertThrows(IllegalArgumentException.class, () -> {
        new Transaction(TransactionType.DEPOSIT, -100);
    });
}

@Test
void testNullTransactionTypeShouldThrowException() {
    // کشف خطای پذیرش null type
    assertThrows(IllegalArgumentException.class, () -> {
        new Transaction(null, 100);
    });
}

@Test
void testTransactionEquality() {
    // کشف خطای نبود equals()
    Transaction t1 = new Transaction(TransactionType.DEPOSIT, 100);
    Transaction t2 = new Transaction(TransactionType.DEPOSIT, 100);
    assertEquals(t1, t2);
}
```

**نتیجه قبل از اصلاح:** 4 تست FAILED  
**نتیجه بعد از اصلاح:** همه 13 تست PASSED ✅

**اصلاحات انجام شده:**

- اضافه کردن validation برای مقادیر منفی و null در constructor
- پیاده‌سازی `equals()` و `hashCode()` methods

### سوال سوم - مشکلات نوشتن تست بعد از برنامه

**مشکل ۱:** **Test Bias** - وقتی کد قبلاً نوشته شده، تست‌ها معمولاً فقط روی عملکرد موجود متمرکز می‌شوند و edge cases فراموش می‌شوند.

**مشکل ۲:** **False Confidence** - پاس شدن تست‌های محدود احساس امنیت کاذب ایجاد می‌کند، در حالی که خطاهای جدی ممکن است همچنان وجود داشته باشند.

**مشکل ۳:** **Design Inflexibility** - کد بدون در نظر گیری testability نوشته می‌شود، که بعداً اضافه کردن تست‌های جامع را دشوار و گاهی غیرممکن می‌سازد.
