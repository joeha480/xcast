<?xml version="1.0" encoding="UTF-8"?>
<!--
		Joel Håkansson, TPB
		Version 2005-07-12
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml"
	xmlns:v="urn:schemas-microsoft-com:vml"
	xmlns:w10="urn:schemas-microsoft-com:office:word"
	xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core"
	xmlns:aml="http://schemas.microsoft.com/aml/2001/core"
	xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint"
	xmlns:o="urn:schemas-microsoft-com:office:office"
	xmlns:st1="urn:schemas-microsoft-com:office:smarttags"
	exclude-result-prefixes="w v w10 sl aml wx o st1">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="/">
	<xsl:processing-instruction name="mso-application">progid="Word.Document"</xsl:processing-instruction>
		<xsl:apply-templates/>
	</xsl:template>
	
<!--	<xsl:template match="w:wordDocument">
		<w:wordDocument>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</w:wordDocument>
	</xsl:template>-->
	
	<xsl:template match="w:t[preceding::w:proofErr[1][@w:type='spellStart']]">
		<w:rPr><w:rStyle w:val="felstavatOrd"/></w:rPr>
		<xsl:call-template name="copy"/>
	</xsl:template>
	
	<xsl:template match="w:styles">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
			<w:style w:type="character" w:styleId="felstavadeOrd">
				<w:name w:val="felstavadeOrd"/>
				<w:semiHidden/>
				<w:rsid w:val="00E41188"/>
			</w:style>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:call-template name="copy"/>
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
