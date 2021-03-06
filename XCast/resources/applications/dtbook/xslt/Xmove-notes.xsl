<?xml version="1.0"?>
<!--<html>
		<p><b>Move-notes:</b></p>
		<p>Skapar <b>rearmatter</b> och flyttar alla <b>note</b> dit</p>
    </html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="book">
		<xsl:element name="{name()}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates/>
			<xsl:if test="count(rearmatter)=0">
				<rearmatter>
					<level1>
						<xsl:apply-templates mode="getNotes" select="//note"/>
					</level1>
				</rearmatter>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="rearmatter">
		<xsl:apply-templates/>
		<level1>
			<xsl:apply-templates mode="getNotes" select="//note"/>
		</level1>
	</xsl:template>
	
	<xsl:template match="note" mode="getNotes">
		<xsl:element name="{name()}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="count(*[self::p])=0">
					<p>
						<xsl:apply-templates/>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<!-- ignorera noter första gången -->
	<xsl:template match="note"/>
	
	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
