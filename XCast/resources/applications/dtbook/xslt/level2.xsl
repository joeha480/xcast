<?xml version="1.0" encoding="utf-8"?>
<!--<html><p><b>Level2:</b></p>
			<p>Lägger till <b>level2</b> runt <b>h2</b> <br>
				och efterföljande innehåll <br>
				fram till nästa <b>hx</b> på samma <br>
				eller högre nivå</p>
	</html>-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="h2">
		<xsl:choose>
			<xsl:when test="parent::level2">
				<xsl:call-template name="level"/>
			</xsl:when>
			<xsl:otherwise>
				<level2>
					<xsl:call-template name="level"/>
				</level2>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:if test="count(preceding-sibling::h2)=0">
			<xsl:call-template name="copy"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="level">
		<xsl:call-template name="copy"/>
		<xsl:for-each select="following-sibling::*[generate-id(preceding-sibling::h2[1])=generate-id(current()) and not(self::h2)]">
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
