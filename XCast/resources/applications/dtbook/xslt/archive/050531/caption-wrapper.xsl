<?xml version="1.0"?>
<!--
		Caption-wrapper
		Joel Håkansson, TPB
		Version 2005-05-11
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template name="wrap">
  <imggroup>
    <xsl:copy-of select="."/>
    <xsl:for-each select="following-sibling::*[(self::caption or self::img) and preceding-sibling::*[1][self::caption or self::img]]">
      <xsl:copy-of select="."/>
    </xsl:for-each>
  </imggroup>
</xsl:template>

<xsl:template match="caption[not(parent::imggroup) and preceding-sibling::*[1][not(self::caption or self::img)]]">
  <xsl:call-template name="wrap"/>
</xsl:template>

<xsl:template match="img[not(parent::imggroup) and preceding-sibling::*[1][(not(self::caption) or (self::caption and parent::table)) and not(self::img)]]">
  <xsl:call-template name="wrap"/>
</xsl:template>

<xsl:template match="caption[parent::table]">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="imggroup">
  <xsl:copy-of select="."/>
</xsl:template>

<!-- Ignorera övriga, dessa har redan behandlats -->
<xsl:template match="img|caption"/>

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
