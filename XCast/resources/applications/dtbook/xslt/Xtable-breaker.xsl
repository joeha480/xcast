<?xml version="1.0" encoding="UTF-8"?>
<!--<html>
		<p><b>Table-breaker:</b></p>
		<p><b>table</b> innuti <b>p</b> läggs efter <b>p</b></p>
    </html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-08-31 09:16
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>

	<xsl:template match="/">
	    <xsl:comment>Table breaker, version 2005-08-31 09:16</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="p">
		<p>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()[not(self::table)]"/>
		</p>
		<xsl:for-each select="table">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
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
