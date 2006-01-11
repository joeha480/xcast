<?xml version="1.0" encoding="utf-8"?>
<!--<html><p><b>Level6:</b></p>
			<p>Lägger till <b>level6</b> runt <b>h6</b> <br>
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
	
	<xsl:template match="h6">
		<xsl:choose>
			<xsl:when test="parent::level6">
				<xsl:call-template name="level"/>
			</xsl:when>
			<xsl:otherwise>
				<level6>
					<xsl:call-template name="level"/>
				</level6>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:if test="count(preceding-sibling::h6)=0">
			<xsl:call-template name="copy"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="level">
		<xsl:call-template name="copy"/>
		<xsl:for-each select="following-sibling::*[generate-id(preceding-sibling::h6[1])=generate-id(current()) and not(self::h6)]">
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
