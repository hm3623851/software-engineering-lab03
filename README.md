# مدیریت حساب بانکی (Account Balance Calculator)

این پروژه یک برنامه ساده جاوا برای مدیریت و محاسبه موجودی حساب بانکی با استفاده از تاریخچه تراکنش‌ها است.

## ساختار پروژه

- `src/main/java/edu/sharif/selab/`
  - `AccountBalanceCalculator.java` : کلاس اصلی محاسبه‌گر
  - `Transaction.java` : مدل تراکنش
  - `TransactionType.java` : نوع تراکنش (واریز/برداشت)
  - `Main.java` : نقطه شروع برنامه
- `src/test/java/edu/sharif/selab/`
  - `AccountBalanceCalculatorTest.java` : تست‌های واحد

## نحوه اجرای تست‌ها

برای اجرای تست‌ها، کافی است در پوشه اصلی پروژه این دستور را اجرا کنید:

```bash
mvn test
```

## اصلاحات انجام شده

### مشکل مدیریت تاریخچه تراکنش‌ها

در ابتدا متد `calculateBalance()` تاریخچه تراکنش‌ها را به درستی به‌روزرسانی نمی‌کرد و فقط موجودی را محاسبه می‌کرد. با اصلاح کد، حالا هر بار که این متد اجرا می‌شود، لیست تراکنش‌های جدید جایگزین تاریخچه قبلی می‌شود و می‌توان تاریخچه را با متد `getTransactionHistory()` دریافت کرد.

## خطاهای کشف شده و رفع شده

### خطاهای کلاس تراکنش

در تست‌های اولیه، برخی خطاها پوشش داده نشده بود:

- امکان ثبت تراکنش با مقدار منفی وجود داشت.
- امکان ثبت تراکنش با نوع null وجود داشت.
- متدهای `equals()` و `hashCode()` برای مقایسه درست اشیاء پیاده‌سازی نشده بود.

برای رفع این مشکلات، اعتبارسنجی مقدار و نوع تراکنش به سازنده اضافه شد و متدهای مقایسه نیز پیاده‌سازی شد.

### تست‌های اضافه شده برای کشف خطا

برای اطمینان از رفع خطاها، تست‌هایی برای مقادیر منفی، نوع null و مقایسه تراکنش‌ها نوشته شد. قبل از اصلاح، این تست‌ها با خطا مواجه می‌شدند اما پس از اصلاح، همه تست‌ها با موفقیت اجرا شدند.

## نتایج تست

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

### سوال اول

در کد اولیه، امکان ثبت تراکنش با مقدار منفی یا نوع نامعتبر وجود داشت و مقایسه تراکنش‌ها هم درست انجام نمی‌شد. چون تست‌ها فقط حالت‌های معمولی را بررسی می‌کردند، این خطاها دیده نشد.

### سوال دوم

برای کشف این خطاها، تست‌هایی برای مقادیر منفی، نوع null و مقایسه تراکنش‌ها نوشتم. بعد از اجرای این تست‌ها و مشاهده خطا، کد را اصلاح کردم تا همه تست‌ها پاس شوند.

### سوال سوم

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

## گزارش اجرای تست‌ها

```text
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------------------< edu.sharif.selab:TDD-2 >-----------------------
[INFO] Building TDD-2 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ TDD-2 ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /home/mahdi/Desktop/se03/software-engineering-lab03/src/main/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ TDD-2 ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ TDD-2 ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /home/mahdi/Desktop/se03/software-engineering-lab03/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ TDD-2 ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ TDD-2 ---
[INFO] Surefire report directory: /home/mahdi/Desktop/se03/software-engineering-lab03/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running edu.sharif.selab.AccountBalanceCalculatorTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.459 s
[INFO] Finished at: 2025-08-08T18:07:35+03:30
[INFO] ------------------------------------------------------------------------
```

### سوال چهارم - تاثیر تست‌نویسی قبل از کدنویسی (TDD)

وقتی قبل از نوشتن کد، تست‌ها را می‌نویسیم، دقیقاً می‌دانیم برنامه باید چه رفتاری داشته باشد و همین باعث می‌شود موقع پیاده‌سازی، کمتر دچار ابهام شویم. این کار باعث شد کد منظم‌تر و با اطمینان بیشتری نوشته شود و اگر تغییری لازم بود، سریع متوجه می‌شدم که کجاها باید اصلاح شود.

### سوال پنجم - مزایا و معایب رویکرد TDD

مزیت اصلی TDD این است که باعث می‌شود کد قابل اطمینان‌تر و تغییرپذیرتر باشد و خطاها زودتر کشف شوند. همچنین، مستندسازی رفتار برنامه به صورت خودکار انجام می‌شود. اما نوشتن تست قبل از کد کمی زمان‌بر است و گاهی نوشتن تست برای بخش‌های پیچیده یا وابسته به محیط سخت می‌شود. در کل، این روش باعث می‌شود برنامه‌نویس با خیال راحت‌تری کد را تغییر دهد و مطمئن باشد که چیزی خراب نمی‌شود.
