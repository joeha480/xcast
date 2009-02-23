<?xml version="1.0" encoding="UTF-8"?>
<!--<html><p><b>Remove-empty-attributes:</b></p>
		  <p>Tar bort attribut som är tomma.</p>
	</html>
-->
<!--
		Joel Håkansson, TPB
		Version 2007-04-24
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>

	<xsl:template match="/">
	    <xsl:comment>Remove-empty-attributes, version 2007-04-24</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:call-template name="copy"/>
	</xsl:template>
	
	<xsl:template name="copy">
		<xsl:element name="{name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="@*">
		<xsl:if test="normalize-space(.)!=''">
			<xsl:copy-of select="."/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
