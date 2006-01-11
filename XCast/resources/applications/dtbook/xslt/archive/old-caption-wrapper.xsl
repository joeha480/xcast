<?xml version="1.0"?>
<!--
		Caption-wrapper
		Joel Håkansson, TPB
		Version 2005-04-19
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no"/>

<xsl:template match="caption[not(parent::imggroup) and not(parent::table)]">
  <imggroup>
    <xsl:for-each select="preceding-sibling::*[1][self::img]">
      <xsl:call-template name="copy"/>
    </xsl:for-each>
    <xsl:call-template name="copy"/>
  </imggroup>
</xsl:template>

<xsl:template match="img[not(parent::imggroup) and following-sibling::*[1][self::caption]]"/>

<xsl:template match="img[not(parent::imggroup) and following-sibling::*[1][not(self::caption)]]">
  <imggroup>
    <xsl:call-template name="copy"/>
  </imggroup>
</xsl:template>

<xsl:template match="img">
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template match="text()">
	<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="*">
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="copy">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*"><xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
  	<xsl:apply-templates/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
