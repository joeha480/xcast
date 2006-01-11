<?xml version="1.0" encoding="UTF-8"?>
<!--<html><p><b>Wrapper-blockquote:</b></p>
			<p>Grupper av <b>blockquote</b> blir <b>p</b> <br>
				omgivna av ett <b>blockquote</b></p>
	</html>
-->
<!--	
		Joel Håkansson, TPB
		Version 2005-09-13
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<!-- Behandla blockquote som inte har några andra föregående syskon -->
<!-- <xsl:template match="*[position()=1 and self::blockquote and not(parent::blockquote)]" priority="10"> -->
<xsl:template match="*[position()=1 and self::blockquote]">
	<blockquote>
		<xsl:for-each select=".|following-sibling::blockquote[count(preceding-sibling::*[not(self::blockquote)])=0]">
			<p><xsl:apply-templates/></p>
		</xsl:for-each>
	</blockquote>
</xsl:template>

<!-- Behandla inte övriga blockquote här -->
<!-- <xsl:template match="blockquote[not(parent::blockquote)]" priority="5"/> -->
<xsl:template match="blockquote"/>

<!-- Behandla den nod som föregår en blockquote speciellt -->
<xsl:template match="*[not(self::blockquote) and following-sibling::*[1][self::blockquote]]">
	<xsl:call-template name="copy"/>
	<blockquote>
		<xsl:for-each select="following-sibling::blockquote[generate-id(preceding-sibling::*[not(self::blockquote)][1])=generate-id(current())]">
			<p>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates/>
			</p>
		</xsl:for-each>
	</blockquote>
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
