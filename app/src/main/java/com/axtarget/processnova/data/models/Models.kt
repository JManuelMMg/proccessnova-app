package com.axtarget.processnova.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

// ==================== AUTENTICACIÓN ====================

data class LoginRequest(
    val username: String,
    val password: String,
    val csrfmiddlewaretoken: String = ""
)

data class LoginResponse(
    val success: Boolean,
    @SerializedName("user_name") val userName: String? = null,
    @SerializedName("user_email") val userEmail: String? = null,
    @SerializedName("org_name") val orgName: String? = null,
    val message: String? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password1: String,
    val password2: String,
    @SerializedName("organization_name") val organizationName: String,
    val rfc: String
)

// ==================== DASHBOARD ====================

data class DashboardData(
    @SerializedName("today_sales") val todaySales: Double = 0.0,
    @SerializedName("critical_stock") val criticalStock: Int = 0,
    @SerializedName("new_customers") val newCustomers: Int = 0,
    @SerializedName("pending_alerts") val pendingAlerts: Int = 0,
    @SerializedName("sales_chart") val salesChart: List<SalesChartPoint> = emptyList(),
    @SerializedName("recent_sales") val recentSales: List<SaleSummary> = emptyList()
)

data class SalesChartPoint(
    val date: String = "",
    val amount: Double = 0.0
)

data class SaleSummary(
    val number: String = "",
    val customer: String = "",
    val total: Double = 0.0,
    val date: String = ""
)

// ==================== INVENTARIO ====================

data class Product(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val type: String = "product",
    val category: String = "",
    @SerializedName("category_id") val categoryId: Int? = null,
    val sku: String = "",
    val barcode: String = "",
    @SerializedName("price") val salePrice: Double = 0.0,
    @SerializedName("cost") val costPrice: Double = 0.0,
    @SerializedName("tax_rate") val taxRate: Double = 16.0,
    val weight: Double = 0.0,
    val dimensions: String = "",
    @SerializedName("image") val imageUrl: String = "",
    @SerializedName("current_stock") val currentStock: Int = 0,
    val stock: Int = 0, // Fallback for sales cache API
    @SerializedName("min_stock") val minStock: Int = 0,
    @SerializedName("max_stock") val maxStock: Int = 0,
    val warehouse: String = "",
    @SerializedName("is_active") val isActive: Boolean = true
) {
    val stockStatus: StockStatus
        get() = when {
            currentStock <= 0 -> StockStatus.CRITICAL
            currentStock <= minStock -> StockStatus.LOW
            else -> StockStatus.OK
        }

    val priceWithTax: Double
        get() = salePrice * (1 + taxRate / 100)
}

enum class StockStatus { OK, LOW, CRITICAL }

data class Category(
    val id: Int = 0,
    val name: String = "",
    val description: String = ""
)

data class StockMovement(
    val id: Int = 0,
    @SerializedName("product_id") val productId: Int = 0,
    @SerializedName("product_name") val productName: String = "",
    val type: String = "",
    val quantity: Int = 0,
    val date: String = "",
    val reference: String = "",
    val notes: String = ""
)

data class Supplier(
    val id: Int = 0,
    val name: String = "",
    val contact: String = "",
    val phone: String = "",
    val email: String = "",
    val rfc: String = "",
    val address: String = ""
)

data class PurchaseOrder(
    val id: Int = 0,
    val number: String = "",
    val supplier: String = "",
    val status: String = "borrador",
    val total: Double = 0.0,
    val date: String = "",
    @SerializedName("expected_date") val expectedDate: String = ""
)

data class QuickCreateRequest(
    val name: String,
    val category_id: Int? = null,
    val sale_price: Double = 0.0,
    val cost_price: Double = 0.0,
    val sku: String = "",
    val barcode: String = ""
)

data class AddStockRequest(
    val product_id: Int,
    val quantity: Int,
    val notes: String = ""
)

data class CreateCategoryRequest(
    val name: String,
    val description: String = ""
)

// ==================== VENTAS / POS ====================

data class CartItem(
    @SerializedName("product_id") val productId: Int = 0,
    @SerializedName("product_name") val name: String = "",
    @SerializedName("unit_price") val price: Double = 0.0,
    val quantity: Int = 1,
    @SerializedName("tax_rate") val taxRate: Double = 16.0,
    @SerializedName("tax_amount") val taxAmount: Double = 0.0,
    val subtotal: Double = 0.0,
    val total: Double = 0.0
) {
    val totalWithTax: Double get() = if (total > 0) total else subtotal + taxAmount
}

data class Cart(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val discount: Double = 0.0,
    val couponCode: String? = null
)

data class AddToCartRequest(
    val product_id: Int,
    val quantity: Int = 1
)

data class UpdateCartItemRequest(
    val product_id: Int,
    val quantity: Int
)

data class RemoveFromCartRequest(
    val product_id: Int
)

data class CheckoutRequest(
    @SerializedName("customer_id") val customer_id: Int? = null,
    @SerializedName("sale_type") val sale_type: String = "pos",
    @SerializedName("payment_method") val payment_method: String = "cash",
    @SerializedName("amount_paid") val amount_paid: Double = 0.0,
    @SerializedName("coupon_code") val coupon_code: String? = null,
    @SerializedName("loyalty_points_used") val loyalty_points_used: Int = 0
)

data class PaymentRequest(
    val method: String,
    val amount: Double,
    val reference: String? = null
)

data class CheckoutResponse(
    val success: Boolean = false,
    @SerializedName("sale_id") val saleId: Int = 0,
    @SerializedName("sale_number") val saleNumber: String = "",
    val total: Double = 0.0,
    @SerializedName("amount_paid") val amountPaid: Double = 0.0,
    val change: Double = 0.0,
    @SerializedName("cart_cleared") val cartCleared: Boolean = false,
    val message: String = ""
)

data class ScanProductRequest(
    val barcode: String
)

data class SwitchBranchRequest(
    val branch_id: Int
)

data class SaleDetail(
    val id: Int = 0,
    val number: String = "",
    val customer: String = "",
    val date: String = "",
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "completed",
    @SerializedName("sale_type") val saleType: String = "pos",
    val items: List<SaleItemDetail> = emptyList(),
    val payments: List<SalePaymentDetail> = emptyList()
)

data class SaleItemDetail(
    @SerializedName("product_name") val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val subtotal: Double = 0.0
)

data class SalePaymentDetail(
    val method: String = "",
    val amount: Double = 0.0
)

// ==================== CRM ====================

data class Customer(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val rfc: String = "",
    val address: String = "",
    @SerializedName("last_purchase") val lastPurchase: String = "",
    @SerializedName("lifetime_value") val lifetimeValue: Double = 0.0,
    val score: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class Lead(
    val id: Int = 0,
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val status: String = "nuevo",
    val source: String = "",
    val score: Int = 0,
    val notes: String = ""
)

data class Opportunity(
    val id: Int = 0,
    val name: String = "",
    val customer: String = "",
    val amount: Double = 0.0,
    val probability: Int = 0,
    val stage: String = "prospectacion",
    @SerializedName("expected_close_date") val expectedCloseDate: String = "",
    val notes: String = ""
) {
    val weightedAmount: Double get() = amount * (probability / 100.0)
}

data class Campaign(
    val id: Int = 0,
    val name: String = "",
    val type: String = "email",
    val status: String = "borrador",
    val budget: Double = 0.0,
    val spent: Double = 0.0,
    val roi: Double = 0.0,
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("end_date") val endDate: String = ""
)

data class Interaction(
    val id: Int = 0,
    @SerializedName("customer_id") val customerId: Int = 0,
    val type: String = "",
    val subject: String = "",
    val notes: String = "",
    val date: String = ""
)

// ==================== FINANZAS ====================

data class Account(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val bank: String = "",
    val balance: Double = 0.0,
    val currency: String = "MXN"
)

data class Transaction(
    val id: Int = 0,
    val date: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val account: String = "",
    val type: String = ""
)

data class Invoice(
    val id: Int = 0,
    val number: String = "",
    val customer: String = "",
    val amount: Double = 0.0,
    val status: String = "pendiente",
    val date: String = "",
    val dueDate: String = ""
)

data class FinanceSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val profitMargin: Double = 0.0,
    val taxes: Double = 0.0,
    val accounts: List<Account> = emptyList()
)

// ==================== RRHH ====================

data class Employee(
    val id: Int = 0,
    @SerializedName("fullName") val fullName: String = "",
    val position: String = "",
    val department: String = "",
    val email: String = "",
    val phone: String = "",
    val rfc: String = "",
    val nss: String = "",
    val curp: String = "",
    val clabe: String = "",
    @SerializedName("hire_date") val hireDate: String = "",
    @SerializedName("pay_frequency") val payFrequency: String = "quincenal",
    val salary: Double = 0.0,
    val status: String = "activo"
)

data class AttendanceRecord(
    val id: Int = 0,
    @SerializedName("employee_id") val employeeId: Int = 0,
    @SerializedName("employee_name") val employeeName: String = "",
    @SerializedName("check_in") val checkIn: String = "",
    @SerializedName("check_out") val checkOut: String = "",
    @SerializedName("hours_worked") val hoursWorked: Double = 0.0,
    val date: String = ""
)

data class PayrollEntry(
    val id: Int = 0,
    @SerializedName("employee_name") val employeeName: String = "",
    val period: String = "",
    val perceptions: Double = 0.0,
    val deductions: Double = 0.0,
    @SerializedName("net_pay") val netPay: Double = 0.0,
    val status: String = "pendiente"
)

data class Department(
    val id: Int = 0,
    val name: String = "",
    val manager: String = "",
    @SerializedName("employee_count") val employeeCount: Int = 0
)

// ==================== LOGÍSTICA ====================

data class ShipmentOrder(
    val id: Int = 0,
    val number: String = "",
    val customer: String = "",
    val address: String = "",
    val status: String = "preparando",
    val date: String = "",
    @SerializedName("delivery_date") val deliveryDate: String = "",
    val driver: String = "",
    val vehicle: String = ""
)

data class Vehicle(
    val id: Int = 0,
    val plate: String = "",
    val model: String = "",
    val driver: String = "",
    val status: String = "disponible"
)

// ==================== NOTIFICACIONES ====================

data class AppNotification(
    val id: Int = 0,
    val type: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("is_read") val isRead: Boolean = false
)

// ==================== IA ====================

data class AiChatRequest(
    val message: String
)

data class AiChatResponse(
    val response: String = "",
    val success: Boolean = true
)

data class AiAnalysisRequest(
    val module: String
)

// ==================== ROOM (OFFLINE) ====================

@Entity(tableName = "cached_products")
data class CachedProduct(
    @PrimaryKey val id: Int,
    val name: String,
    val sku: String,
    val barcode: String,
    val salePrice: Double,
    val currentStock: Int,
    val category: String,
    val imageUrl: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_customers")
data class CachedCustomer(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val rfc: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_sales")
data class OfflineSale(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val items: String, // JSON de items
    val total: Double,
    val customerId: Int?,
    val saleType: String,
    val payments: String, // JSON de pagos
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

