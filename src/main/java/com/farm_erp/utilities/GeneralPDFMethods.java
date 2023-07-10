package com.farm_erp.utilities;

import java.io.IOException;
import java.net.MalformedURLException;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.domains.Business;
import com.farm_erp.statics.Constants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

@Service
public class GeneralPDFMethods {

	public static void addBusinessInformation(User user, Document document) {
		byte[] img = null;
		if (user.business.logo == null) {
			img = Base64.decodeBase64(Constants.defaultLogo);
		} else {
			img = user.business.logo.data;
		}

		Image image = null;
		try {
			image = Image.getInstance(img);
		} catch (BadElementException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			image.scalePercent(50f);
			PdfPTable tab = new PdfPTable(2);
			tab.setHorizontalAlignment(0);
			tab.setWidthPercentage(100.0f);
			tab.setWidths(new int[] { 30, 70 });

			PdfPCell cell1 = new PdfPCell(image);
			cell1.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

			Font font = new Font();
			font.setStyle(Font.BOLD);

			PdfPCell cell2 = new PdfPCell(new Phrase(user.business.name, font));

			cell2.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

			tab.addCell(cell1);
			tab.addCell(cell2);

			document.add(tab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Font pageHeaderFont() {
		Font font = new Font();
		font.setSize(18);
		font.setStyle(Font.BOLD);
		return font;
	}

	public static void addBusinessInformation(Business business, Document document) {
		byte[] img = null;
		if (business.logo == null) {
			img = Base64.decodeBase64(Constants.defaultLogo);
		} else {
			img = business.logo.data;
		}

		Image image = null;
		try {
			image = Image.getInstance(img);
		} catch (BadElementException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			image.scalePercent(15f);

			PdfPTable tab = new PdfPTable(1);
			tab.setHorizontalAlignment(0);
			tab.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(image);
			cell1.setBorder(0);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

			Font font = new Font();
			font.setStyle(Font.BOLD);
			font.setSize(13);

			PdfPCell cell2 = new PdfPCell(new Phrase(business.name, font));

			cell2.setBorder(0);
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

			tab.addCell(cell1);
//			tab.addCell(cell2);

			document.add(tab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	public static Font pageTitleFont() {
		Font font = new Font();
		font.setSize(14);
		font.setStyle(Font.BOLD);
		return font;
	}

	public static Font italicFont() {
		Font font = new Font();
		font.setStyle(Font.ITALIC);
		return font;
	}

	public static Font tableHeaderFont() {
		Font font = new Font();
		font.setStyle(Font.NORMAL);
		font.setColor(BaseColor.WHITE);
		font.setSize(9);

		return font;
	}

	public static Font tableHeaderSmallFont() {
		Font font = new Font();
		font.setStyle(Font.NORMAL);
		font.setColor(BaseColor.WHITE);
		font.setSize(7);

		return font;
	}

	public static Font tableGroupHeaderFont() {
		Font font = new Font();
		font.setStyle(Font.BOLD);
		font.setSize(9);

		return font;
	}

	public static Font tableGroupHeaderSmallFont() {
		Font font = new Font();
		font.setStyle(Font.NORMAL);
		font.setSize(9);

		return font;
	}

	public static Font smallFont() {
		Font font = new Font();
		font.setStyle(Font.BOLD);
		font.setSize(7);

		return font;
	}



	public static Font tableTextFont() {
		Font font = new Font();
		font.setSize(8);

		return font;
	}

	public static Font tableTextSmallFont() {
		Font font = new Font();
		font.setSize(6);

		return font;
	}

	public static Font waterMarkFont() {
		return new Font(Font.FontFamily.TIMES_ROMAN, 40, Font.BOLD, BaseColor.GRAY);
	}
}
