<?xml version="1.0" encoding="UTF-8"?>
<!--<html>
		<p><b>InDesign fix</b></p>
		<p>Tar bort dubletter av <b>pagenum</b></p>
	</html>-->
<!--
		Joel Håkansson, TPB
		Version 2006-12-21
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="/">
	    <xsl:comment>InDesign fix, version 2006-12-21</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="pagenum">
		<xsl:if test="not(preceding::pagenum[@id=current()/@id])">
			<xsl:call-template name="copy"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="*">
		<xsl:call-template name="copy"/>
	</xsl:template>
	
	<xsl:template name="copy">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
