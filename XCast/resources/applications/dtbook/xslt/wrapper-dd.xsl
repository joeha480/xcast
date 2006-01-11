<?xml version="1.0" encoding="UTF-8"?>
<!--<html><p><b>Wrapper-dd:</b></p>
			<p>Grupper av <b>dd</b> omges med <b>dl</b></p>
	</html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:variable name="type" select="'pl'"/>

<!-- Behandla listor som inte har några andra föregående syskon -->
<xsl:template match="*[position()=1 and self::dd]">
	<dl>
		<xsl:for-each select=".|following-sibling::dd[count(preceding-sibling::*[not(self::dd)])=0]">
			<xsl:call-template name="copy"/>
		</xsl:for-each>
	</dl>
</xsl:template>

<!-- Behandla inte övriga listnoder här -->
<xsl:template match="dd"/>

<!-- Behandla den nod som föregår en lista speciellt -->
<xsl:template match="*[not(self::dd) and following-sibling::*[1][self::dd]]">
	<xsl:call-template name="copy"/>
	<dl>
		<xsl:for-each select="following-sibling::dd[generate-id(preceding-sibling::*[not(self::dd)][1])=generate-id(current())]">
			<dd>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates/>
			</dd>
		</xsl:for-each>
	</dl>
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
