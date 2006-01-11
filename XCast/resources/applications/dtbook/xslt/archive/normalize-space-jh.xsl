<?xml version="1.0" encoding="UTF-8"?>
<!--
		Tar bort whitespace mellan noder
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="text()">	
		<xsl:value-of select="normalize-space(.)"/>
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
