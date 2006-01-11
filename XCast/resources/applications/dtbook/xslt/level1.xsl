<?xml version="1.0" encoding="utf-8"?>
<!--<html><p><b>Level1:</b></p>
			<p>Lägger till <b>level1</b> runt <b>h1</b> <br>
				och efterföljande innehåll <br>
				fram till nästa <b>h1</b></p>
	</html>-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="h1">
		<xsl:choose>
			<xsl:when test="parent::level1">
				<xsl:call-template name="level"/>
			</xsl:when>
			<xsl:otherwise>
				<level1>
					<xsl:call-template name="level"/>
				</level1>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:if test="count(preceding-sibling::h1)=0">
			<xsl:call-template name="copy"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="level">
		<xsl:call-template name="copy"/>
		<xsl:for-each select="following-sibling::*[generate-id(preceding-sibling::h1[1])=generate-id(current()) and not(self::h1)]">
			<xsl:call-template name="copy"/>
		</xsl:for-each>
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
