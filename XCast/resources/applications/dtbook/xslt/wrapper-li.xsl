<?xml version="1.0" encoding="UTF-8"?>
<!--<html><p><b>Wrapper-li:</b></p>
			<p>Grupper av <b>li</b> omges med <b>list</b></p>
	</html>
-->
<!--
		
-->
<!--	
		Joel Håkansson, TPB
		Version 2005-09-13
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:variable name="type" select="'pl'"/>

<!-- Behandla listor som inte har några andra föregående syskon -->
<xsl:template match="*[position()=1 and self::li and not(parent::list)]" priority="10">
	<list type="{$type}">
		<xsl:for-each select=".|following-sibling::li[count(preceding-sibling::*[not(self::li)])=0]">
			<xsl:call-template name="copy"/>
		</xsl:for-each>
	</list>
</xsl:template>

<!-- Behandla inte övriga listnoder här -->
<xsl:template match="li[not(parent::list)]" priority="5"/>

<!-- Behandla den nod som föregår en lista speciellt -->
<xsl:template match="*[not(self::li) and following-sibling::*[1][self::li]]">
	<xsl:call-template name="copy"/>
	<list type="{$type}">
		<xsl:for-each select="following-sibling::li[generate-id(preceding-sibling::*[not(self::li)][1])=generate-id(current())]">
			<li>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates/>
			</li>
		</xsl:for-each>
	</list>
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
