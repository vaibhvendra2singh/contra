import os
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Image as RLImage, Table, TableStyle, PageBreak
)
from reportlab.pdfgen import canvas

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
            self.drawString(40, 760, "Jira Project OM2 — Live Demonstration & Teacher Defense Manual")
            self.drawRightString(572, 760, "Team Contra · CSET455 Agile")
            self.setStrokeColor(colors.HexColor("#dddddd"))
            self.setLineWidth(0.5)
            self.line(40, 754, 572, 754)
            
        # Footer
        self.setStrokeColor(colors.HexColor("#dddddd"))
        self.setLineWidth(0.5)
        self.line(40, 42, 572, 42)
        self.drawString(40, 30, "Live Verified on vaibhvendra-vusimq0m.atlassian.net · Project: OM2")
        self.drawRightString(572, 30, f"Page {self._pageNumber} of {page_count}")
        self.restoreState()

def create_jira_pdf(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=40,
        rightMargin=40,
        topMargin=48,
        bottomMargin=48
    )

    styles = getSampleStyleSheet()
    
    C_DARK = colors.HexColor("#0052cc")     # Atlassian blue
    C_ACCENT = colors.HexColor("#ff5630")   # Red/Orange accent
    C_BG = colors.HexColor("#f4f5f7")       # Jira subtle grey
    C_TEXT = colors.HexColor("#172b4d")     # Jira dark text
    C_MUTED = colors.HexColor("#5e6c84")    # Jira muted text
    C_BORDER = colors.HexColor("#dfe1e6")

    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=22,
        leading=26,
        textColor=C_DARK,
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
        textColor=colors.HexColor("#091e42")
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
    story.append(Paragraph("Jira Project OM2 Walkthrough Guide", title_style))
    story.append(Paragraph("<b>Agile Artifacts, Scrum Board & Quality Gates Live Demonstration</b><br/>"
                           "Site: <code>vaibhvendra-vusimq0m.atlassian.net</code> · Board ID: 172 · Team: Contra", subtitle_style))
    
    meta_data = [
        [
            Paragraph("<b>Project:</b> E-Commerce Feature Rollout", body_style),
            Paragraph("<b>Project Key:</b> OM2", body_style),
            Paragraph("<b>Total Work Items:</b> 67 (All Done)", body_style)
        ],
        [
            Paragraph("<b>Total Story Points:</b> 116 Pts", body_style),
            Paragraph("<b>Scrum Columns:</b> 5 Workflow Stages", body_style),
            Paragraph("<b>Hierarchy:</b> Epic → Story → QA Subtask", body_style)
        ]
    ]
    meta_table = Table(meta_data, colWidths=[180, 180, 172])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#deebff")),
        ('BOX', (0,0), (-1,-1), 1, C_BORDER),
        ('INNERGRID', (0,0), (-1,-1), 0.5, C_BORDER),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    base_shot_dir = "/Users/vaibhvendra/.gemini/antigravity/brain/a1093d24-160f-4a63-a21a-59e11e9d123a/.system_generated/steps"

    steps = [
        {
            "step_num": "1",
            "title": "Jira Project Summary & 100% Completion Overview",
            "img_path": os.path.join(base_shot_dir, "24", "media_0.png"),
            "what_to_see": "The Jira Project Summary dashboard for OM2. Displays '67 completed', '0 due soon', and a 100% complete donut chart. Recent activity verifies completed sprint epics.",
            "cursor_action": "Pointer points directly to the 67 completed badge and status overview donut chart.",
            "teacher_script": "\"Here is our live Jira Cloud project OM2. As you can see on the Summary dashboard, all 67 work items across all 8 epics are marked Done, with zero pending items.\"",
            "technical_notes": "URL: /jira/software/projects/OM2/summary. Tracks 8 Epics, 25 User Stories, 9 Dev Tasks, and 25 QA subtasks."
        },
        {
            "step_num": "2",
            "title": "Scrum Board & The 5-Stage Quality Workflow",
            "img_path": os.path.join(base_shot_dir, "48", "media_0.png"),
            "what_to_see": "The Scrum Board (Board ID 172) displaying the customized 5-column workflow: To Do → In Progress → In Review → QA → Done.",
            "cursor_action": "Pointer highlights the top navigation bar 'Board' tab and traces the 5 board columns from left to right.",
            "teacher_script": "\"Our team implemented a professional 5-column Scrum workflow. Notice the dedicated QA column: no story can reach 'Done' without formal QA verification.\"",
            "technical_notes": "Column configuration ensures quality gating. The linked QA subtask is assigned before moving to QA column."
        },
        {
            "step_num": "3",
            "title": "Product Backlog & Fibonacci Story Point Estimation",
            "img_path": os.path.join(base_shot_dir, "72", "media_0.png"),
            "what_to_see": "The Backlog view showing 116 total story points. Each story card clearly displays its Fibonacci point estimate, epic tag, and child QA task completion status.",
            "cursor_action": "Pointer highlights story OM2-9 showing 5 story points, epic badge, and '1 of 1 child work item complete'.",
            "teacher_script": "\"In the Backlog view, we estimated all user stories using Fibonacci story points (totaling 116 points). Every story has an associated child QA task.\"",
            "technical_notes": "Story point field customfield_10016. Sprints were planned as: Sprint 1 (29 pts), Sprint 2 (27 pts), Sprint 3 (60 pts)."
        },
        {
            "step_num": "4",
            "title": "3-Level Agile Hierarchy (Epic → Story → QA Subtask)",
            "img_path": os.path.join(base_shot_dir, "102", "media_0.png"),
            "what_to_see": "The List view expanding the 3-level work breakdown: Epic OM2-1 (Order Creation) contains User Story OM2-9, which contains QA Subtask OM2-34.",
            "cursor_action": "Pointer clicks the expand arrow next to Epic OM2-1 and highlights child story OM2-9 and QA subtask OM2-34.",
            "teacher_script": "\"Here you can clearly see our 3-tier hierarchy: Epics represent high-level capabilities, broken down into User Stories, each guarded by a linked QA Subtask.\"",
            "technical_notes": "Maintains 100% requirements traceability from spec.md down to automated integration test execution."
        },
        {
            "step_num": "5",
            "title": "Story Details: Story Points & Acceptance Criteria (OM2-9)",
            "img_path": os.path.join(base_shot_dir, "120", "media_0.png"),
            "what_to_see": "Issue detail modal for OM2-9. Shows 'Story points: 5', priority 'High', and formal Gherkin/BDD acceptance criteria with checkboxes.",
            "cursor_action": "Pointer highlights 'Story points: 5' in the right-hand planning panel and points to the acceptance criteria checkboxes.",
            "teacher_script": "\"Every story defines clear, testable Acceptance Criteria. Developers cannot declare a story complete unless all acceptance checkboxes are met.\"",
            "technical_notes": "Acceptance criteria define expected state changes (CREATED → PAYMENT_PENDING) and payload schema."
        },
        {
            "step_num": "6",
            "title": "Definition of Done (DoD) & QA Quality Blocker (OM2-34)",
            "img_path": os.path.join(base_shot_dir, "129", "media_0.png"),
            "what_to_see": "Shows the link 'is blocked by OM2-34 (QA Verified - Done)' and the detailed activity comments recording automated test pass evidence.",
            "cursor_action": "Pointer points to 'is blocked by OM2-34' link and the DoD verification comment in the issue activity stream.",
            "teacher_script": "\"To enforce our Definition of Done, every story has an issue link: 'is blocked by' its QA subtask. The comment history includes automated test results as proof.\"",
            "technical_notes": "Link type: Blocks / is blocked by. Corresponds to OrderManagementIntegrationTest and OrderStateMachineTest in codebase."
        }
    ]

    target_w = 480
    target_h = 245

    for idx, s in enumerate(steps):
        step_header = [
            [
                Paragraph(f"<b>JIRA STEP {s['step_num']} OF {len(steps)}</b>", ParagraphStyle('Badge', fontName='Helvetica-Bold', fontSize=8.5, textColor=colors.white)),
                Paragraph(s['title'], step_title_style)
            ]
        ]
        sh_table = Table(step_header, colWidths=[105, 427])
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

        if os.path.exists(s['img_path']):
            try:
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
                Paragraph("<b>Agile & Jira Concept:</b>", tech_label_style),
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
            ('BACKGROUND', (0,3), (-1,3), colors.HexColor("#fff0b3")),
        ]))
        story.append(det_table)

        if idx < len(steps) - 1:
            story.append(PageBreak())

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Jira PDF successfully generated at: {output_path}")

if __name__ == "__main__":
    out_file = "/Users/vaibhvendra/Desktop/clg/project/AGILE PROJECT/Contra_Jira_Live_Demo_Guide.pdf"
    create_jira_pdf(out_file)
