import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Image as RLImage, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas
from PIL import Image as PILImage

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#666666"))
        
        # Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(40, 760, "Contra OMS — Interactive Live Demonstration & Operation Manual")
            self.drawRightString(572, 760, "Team Contra · CSET455 Agile")
            self.setStrokeColor(colors.HexColor("#dddddd"))
            self.setLineWidth(0.5)
            self.line(40, 754, 572, 754)
            
        # Footer
        self.setStrokeColor(colors.HexColor("#dddddd"))
        self.setLineWidth(0.5)
        self.line(40, 42, 572, 42)
        self.drawString(40, 30, "Live Verified on http://localhost:8080 · Jira Cloud Key: OM2")
        self.drawRightString(572, 30, f"Page {self._pageNumber} of {page_count}")
        self.restoreState()

def create_guide_pdf(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=40,
        rightMargin=40,
        topMargin=48,
        bottomMargin=48
    )

    styles = getSampleStyleSheet()
    
    # Custom Palette
    C_DARK = colors.HexColor("#162e2b")
    C_ACCENT = colors.HexColor("#c26d38")
    C_BG = colors.HexColor("#f8f9fa")
    C_TEXT = colors.HexColor("#222222")
    C_MUTED = colors.HexColor("#555555")
    C_BORDER = colors.HexColor("#e0e4e2")
    C_GREEN = colors.HexColor("#2e7d32")
    C_BLUE = colors.HexColor("#1565c0")

    # Typography styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=22,
        leading=26,
        textColor=C_DARK,
        alignment=0,
        spaceAfter=6
    )

    subtitle_style = ParagraphStyle(
        'DocSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=11,
        leading=15,
        textColor=C_MUTED,
        spaceAfter=15
    )

    step_title_style = ParagraphStyle(
        'StepTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=C_DARK,
        spaceBefore=8,
        spaceAfter=4
    )

    body_style = ParagraphStyle(
        'BodyDark',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=C_TEXT
    )

    script_label_style = ParagraphStyle(
        'ScriptLabel',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9,
        leading=12,
        textColor=C_ACCENT
    )

    script_text_style = ParagraphStyle(
        'ScriptText',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=8.5,
        leading=12,
        textColor=colors.HexColor("#111111")
    )

    tech_label_style = ParagraphStyle(
        'TechLabel',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=C_DARK
    )

    tech_text_style = ParagraphStyle(
        'TechText',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=11,
        textColor=C_MUTED
    )

    story = []

    # Title & Metadata Banner
    story.append(Paragraph("Contra Order Management System", title_style))
    story.append(Paragraph("<b>Visual Step-by-Step Operation Guide & Live Teacher Demonstration Manual</b><br/>"
                           "Course: CSET455 Agile Software Development · Team: Contra · Verified on <code>localhost:8080</code>", subtitle_style))
    
    # Metadata Overview Table
    meta_data = [
        [
            Paragraph("<b>Project:</b> E-Commerce OMS + Jira Rollout", body_style),
            Paragraph("<b>Stack:</b> Java 17 · Spring Boot 3 · H2 · JS", body_style),
            Paragraph("<b>Jira Key:</b> OM2 (67/67 Done)", body_style)
        ],
        [
            Paragraph("<b>Automated Tests:</b> 17/17 Passed", body_style),
            Paragraph("<b>Currency:</b> INR (₹)", body_style),
            Paragraph("<b>FSM States:</b> 12 Lifecycle States", body_style)
        ]
    ]
    meta_table = Table(meta_data, colWidths=[180, 180, 172])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#f1f5f3")),
        ('BOX', (0,0), (-1,-1), 1, C_BORDER),
        ('INNERGRID', (0,0), (-1,-1), 0.5, C_BORDER),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    # Base directory for screenshots
    base_shot_dir = "/Users/vaibhvendra/.gemini/antigravity/brain/2d1dea7a-caef-469b-a3d8-5b00fbed9055/.system_generated/steps"

    steps = [
        {
            "step_num": "1",
            "title": "Storefront & Catalog Browsing (INR ₹)",
            "img_path": os.path.join(base_shot_dir, "18", "media_0.png"),
            "what_to_see": "The customer arrives on the responsive Contra storefront. Live inventory displays 3 catalog items in Indian Rupees (₹): Contra Ceramic Mug (₹12), Team T-shirt (₹24), and Canvas Tote (₹18.50).",
            "cursor_action": "Pointer hovers over the catalog cards and targets the 'Contra Ceramic Mug' card.",
            "teacher_script": "\"Here is the Contra storefront running locally on port 8080. Notice all products have real-time inventory counts and are priced in Indian Rupees (₹).\"",
            "technical_notes": "GET /api/products returns active products with availableQuantity = stockQuantity - allocatedQuantity."
        },
        {
            "step_num": "2",
            "title": "Adding Product to Bag with Instant Visual Feedback",
            "img_path": os.path.join(base_shot_dir, "30", "media_0.png"),
            "what_to_see": "Clicking 'Add to bag +' updates the bag badge in the top navigation from 0 to 1 and flashes an instant toast notification: 'Added to your bag'.",
            "cursor_action": "Pointer clicks directly on the 'Add to bag +' button on the Ceramic Mug card.",
            "teacher_script": "\"When the user selects an item, the client-side cart updates immediately without unnecessary page reloads.\"",
            "technical_notes": "Cart state is maintained in client-side memory; no DB locks are acquired until checkout to avoid stale reservations."
        },
        {
            "step_num": "3",
            "title": "Cart Slide-out & Order Total Calculation",
            "img_path": os.path.join(base_shot_dir, "45", "media_0.png"),
            "what_to_see": "The cart modal opens, showing the selected line items, unit prices, quantity multipliers, and calculated order subtotal of ₹12.00.",
            "cursor_action": "Pointer clicks the top-right 'Cart (1)' button to trigger the checkout modal.",
            "teacher_script": "\"Opening the cart presents the customer with a transparent order total and prompts for shipping and payment information.\"",
            "technical_notes": "Cart subtotal dynamically calculates item quantity * price and verifies against max allowed cart limits."
        },
        {
            "step_num": "4",
            "title": "Customer Checkout & Mock Payment Submission",
            "img_path": os.path.join(base_shot_dir, "84", "media_0.png"),
            "what_to_see": "Customer enters name ('Aarav Sharma'), email ('aarav.sharma@example.com'), selects payment method CARD, and clicks 'Place order'.",
            "cursor_action": "Pointer enters test customer details, selects CARD, and clicks the primary 'Place order' button.",
            "teacher_script": "\"Here we submit the order with CARD payment. The backend invokes our transactional checkout service and MockPaymentGateway.\"",
            "technical_notes": "POST /api/orders processes checkout atomically: acquires pessimistic write lock on Product, verifies stock, transitions CREATED -> PAYMENT_PENDING -> PAID -> INVENTORY_ALLOCATED."
        },
        {
            "step_num": "5",
            "title": "Instant Order Tracking & Immutable Audit Timeline",
            "img_path": os.path.join(base_shot_dir, "93", "media_0.png"),
            "what_to_see": "System automatically opens the Track Order tab for Order #3A9E81A2. Status is INVENTORY ALLOCATED. The audit trail logs all timestamps and actors.",
            "cursor_action": "System auto-populates Order ID into track field; pointer inspects the live lifecycle audit cards.",
            "teacher_script": "\"Every state transition is stored in the order_transitions table with an exact timestamp and actor, providing a 100% auditable history.\"",
            "technical_notes": "Inventory is allocated ONLY after payment capture succeeds. Unpaid orders never consume sellable warehouse stock."
        },
        {
            "step_num": "6",
            "title": "Operations Dashboard & Real-Time Fulfillment Queue",
            "img_path": os.path.join(base_shot_dir, "102", "media_0.png"),
            "what_to_see": "Admin navigates to Operations. The dashboard displays 4 key operational metrics (Total orders, Need attention, Failed payments, In fulfillment) and the pending fulfillment queue.",
            "cursor_action": "Pointer clicks the 'Operations' navigation tab to view the warehouse management workspace.",
            "teacher_script": "\"In the Operations dashboard, warehouse operators have real-time visibility into live orders waiting for fulfillment.\"",
            "technical_notes": "GET /api/admin/dashboard aggregates status counts, detects stuck orders (>24h), and lists orders in INVENTORY_ALLOCATED or FULFILLING."
        },
        {
            "step_num": "7",
            "title": "Warehouse Operator Initiates Fulfillment",
            "img_path": os.path.join(base_shot_dir, "108", "media_0.png"),
            "what_to_see": "Order card displays customer info, items ordered, and an actionable 'Start fulfillment' button.",
            "cursor_action": "Pointer highlights and clicks 'Start fulfillment' on Order #3A9E81A2.",
            "teacher_script": "\"The warehouse worker accepts the order to begin picking and packing. Status changes to FULFILLING.\"",
            "technical_notes": "POST /api/admin/orders/{id}/fulfill triggers Order.transitionTo(FULFILLING). Order cannot be fulfilled without allocated inventory."
        },
        {
            "step_num": "8",
            "title": "Assigning Carrier Tracking Code & Dispatching Shipment",
            "img_path": os.path.join(base_shot_dir, "117", "media_0.png"),
            "what_to_see": "A prompt appears for tracking code ('CT-EXP-2026IN'). Submitting advances the order to SHIPPED.",
            "cursor_action": "Pointer clicks 'Add tracking & ship', submits carrier tracking code in the prompt dialog.",
            "teacher_script": "\"When the parcel leaves the warehouse, carrier tracking is assigned. Notice cancellation is now permanently locked out.\"",
            "technical_notes": "POST /api/admin/orders/{id}/ship records carrier tracking number. Crucial business rule: Once SHIPPED, cancellation is rejected by the FSM."
        },
        {
            "step_num": "9",
            "title": "Carrier Delivery Confirmation",
            "img_path": os.path.join(base_shot_dir, "126", "media_0.png"),
            "what_to_see": "Order advances to DELIVERED. Customer tracking page reflects the delivered state and unlocks post-delivery self-service actions.",
            "cursor_action": "Pointer clicks 'Mark delivered' in the Operations order action bar.",
            "teacher_script": "\"Once the carrier confirms parcel drop-off, the order is marked DELIVERED, which unlocks the return eligibility window.\"",
            "technical_notes": "POST /api/admin/orders/{id}/deliver logs the delivery event and generates automated notification logs for simulated email/SMS."
        },
        {
            "step_num": "10",
            "title": "Customer Self-Service Return Request",
            "img_path": os.path.join(base_shot_dir, "141", "media_0.png"),
            "what_to_see": "Customer revisits the tracking page. Because the order is DELIVERED, the 'Request a return' button is active. Clicking moves state to RETURN_REQUESTED.",
            "cursor_action": "Pointer clicks 'Request a return' on the customer tracking timeline.",
            "teacher_script": "\"Our system supports post-delivery returns. The customer initiates the return request right from their tracking timeline.\"",
            "technical_notes": "POST /api/orders/{id}/returns verifies that the order is in DELIVERED status. Any premature return request is rejected by domain logic."
        },
        {
            "step_num": "11",
            "title": "Warehouse Return Receipt & Automated Refund Capture",
            "img_path": os.path.join(base_shot_dir, "168", "media_0.png"),
            "what_to_see": "Operations team receives the returned package ('Receive return') and issues an electronic refund ('Issue refund'). Status updates to REFUNDED.",
            "cursor_action": "Pointer clicks 'Receive return', followed by 'Issue refund'.",
            "teacher_script": "\"The warehouse verifies the physical return, restocking sellable inventory. The operator then issues a refund through the gateway.\"",
            "technical_notes": "POST /api/admin/orders/{id}/returned restocks product quantity. POST /api/orders/{id}/refund invokes PaymentGateway.refund() and stores PaymentAttempt."
        },
        {
            "step_num": "12",
            "title": "Agile Jira Cloud Integration & Dynamic Feature Flags",
            "img_path": os.path.join(base_shot_dir, "177", "media_0.png"),
            "what_to_see": "Shows the 3 database-backed feature flags linked to Jira stories and QA tasks, plus live Jira Cloud Sprint 3 metrics (60/60 points completed).",
            "cursor_action": "Pointer scrolls to Feature Flags and Jira Cloud delivery widgets at the bottom of Operations.",
            "teacher_script": "\"Here is the core of our Agile project: feature flags are explicitly mapped to Jira stories and QA tasks, directly tying live code rollout to verified Jira completion.\"",
            "technical_notes": "Operations dashboard queries Jira Cloud REST API asynchronously. If Jira is offline, OfflineJiraClient seamlessly ensures order operations never break."
        }
    ]

    target_w = 480
    target_h = 245

    for idx, s in enumerate(steps):
        # We put 1 step per page for clarity and high resolution
        step_header = [
            [
                Paragraph(f"<b>STEP {s['step_num']} OF {len(steps)}</b>", ParagraphStyle('Badge', fontName='Helvetica-Bold', fontSize=9, textColor=colors.white)),
                Paragraph(s['title'], step_title_style)
            ]
        ]
        sh_table = Table(step_header, colWidths=[90, 442])
        sh_table.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (0,0), C_DARK),
            ('ALIGN', (0,0), (0,0), 'CENTER'),
            ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
            ('TOPPADDING', (0,0), (0,0), 4),
            ('BOTTOMPADDING', (0,0), (0,0), 4),
            ('LEFTPADDING', (0,0), (-1,-1), 6),
            ('RIGHTPADDING', (0,0), (-1,-1), 6),
        ]))
        story.append(sh_table)
        story.append(Spacer(1, 6))

        # Screenshot insertion
        if os.path.exists(s['img_path']):
            try:
                # Add image
                img_flowable = RLImage(s['img_path'], width=target_w, height=target_h)
                img_box = Table([[img_flowable]], colWidths=[532])
                img_box.setStyle(TableStyle([
                    ('ALIGN', (0,0), (-1,-1), 'CENTER'),
                    ('BOX', (0,0), (-1,-1), 1, C_BORDER),
                    ('TOPPADDING', (0,0), (-1,-1), 3),
                    ('BOTTOMPADDING', (0,0), (-1,-1), 3),
                    ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#fafafa"))
                ]))
                story.append(img_box)
            except Exception as e:
                story.append(Paragraph(f"[Image rendering error: {e}]", body_style))
        else:
            story.append(Paragraph("[Screenshot not found on disk]", body_style))

        story.append(Spacer(1, 8))

        # Operational details table
        details_data = [
            [
                Paragraph("<b>On-Screen View:</b>", tech_label_style),
                Paragraph(s['what_to_see'], tech_text_style)
            ],
            [
                Paragraph("<b>Cursor Pointer Action:</b>", tech_label_style),
                Paragraph(s['cursor_action'], tech_text_style)
            ],
            [
                Paragraph("<b>Under the Hood (Code & Jira):</b>", tech_label_style),
                Paragraph(s['technical_notes'], tech_text_style)
            ],
            [
                Paragraph("<b>Say to Teacher (Script):</b>", script_label_style),
                Paragraph(f"💬 {s['teacher_script']}", script_text_style)
            ]
        ]
        
        det_table = Table(details_data, colWidths=[130, 402])
        det_table.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,-1), C_BG),
            ('BOX', (0,0), (-1,-1), 0.5, C_BORDER),
            ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#eeeeee")),
            ('TOPPADDING', (0,0), (-1,-1), 4),
            ('BOTTOMPADDING', (0,0), (-1,-1), 4),
            ('LEFTPADDING', (0,0), (-1,-1), 8),
            ('RIGHTPADDING', (0,0), (-1,-1), 8),
            ('VALIGN', (0,0), (-1,-1), 'TOP'),
            ('BACKGROUND', (0,3), (-1,3), colors.HexColor("#fff8e7")),  # Highlight script row
        ]))
        story.append(det_table)

        if idx < len(steps) - 1:
            story.append(PageBreak())

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"PDF successfully generated at: {output_path}")

if __name__ == "__main__":
    out_file = "/Users/vaibhvendra/Desktop/clg/project/AGILE PROJECT/Contra_Live_Demo_Visual_Guide.pdf"
    create_guide_pdf(out_file)
