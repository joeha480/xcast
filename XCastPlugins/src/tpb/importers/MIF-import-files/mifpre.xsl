<?xml version="1.0" encoding="utf-8"?>
<!--
		*** MIFPre ***
		(DESC)
		Joel Håkansson, TPB
		Version 2005-05-31
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="ColorCatalog|ConditionCatalog|CombinedFontCatalog|
						ElementDefCatalog|FmtChangeListCatalog|KumihanCatalog|Views|VariableFormats|
						XRefFormats|RulingCatalog|TabStop|TblCatalog"/>
	
	<xsl:template match="TextRectID">
		<xsl:variable name="value" select="@attr1"/>
		<!-- Är detta det första TextRectID med det här värdet på attr1 i detta TextFlow? -->
		<xsl:if test="generate-id(current())=generate-id(ancestor::TextFlow/Para/ParaLine/TextRectID[@attr1=$value][1])">
			<xsl:copy-of select="."/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="MIFXML|TextFlow|Para|ParaLine">
		<xsl:call-template name="copy"/>	
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy-of select="."/>
	</xsl:template>

	<xsl:template name="copy">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
