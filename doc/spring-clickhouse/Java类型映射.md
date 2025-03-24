## Java 类型映射

#### Int | UInt  
- Int8 => byte
- Int16 => short
- Int32 => int
- Int64 => long
- Int128 => String
- Int256 => String
- UInt8 => short
- UInt16 => int
- UInt32 => long
- UInt64 => String
- UInt128 => String
- UInt256 => String

#### Float32 | Float64 | BFloat16
- Float32 => float|String
- Float64 => double|String
- BFloat16 => String
> ClickHouse的浮点型有NaN 和 Inf这样的表达方式，如果有使用到这种的话，只能用字符串表示


#### Decimal
Decimal32 => BigDecimal
Decimal64 => BigDecimal
Decimal128 => BigDecimal
Decimal256 => BigDecimal

#### String
String => String
FixedString => String

#### Date
Date32 => Date | Time
DateTime => Date | Time
DateTime64 => Date | Time

#### Enum
Enum => String

#### Bool
Bool => bool

#### UUID
UUID => Sting

