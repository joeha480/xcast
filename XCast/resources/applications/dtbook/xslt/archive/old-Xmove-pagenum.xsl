<?xml version="1.0"?>
<!--
		*** Move pagenum ***
		Lägger sidnummer som finns innuti hx före
		Joel Håkansson, TPB
		Version 2005-05-10
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no"/>

<xsl:template match="h1|h2|h3|h4|h5|h6|hd">
  <!-- kopiera alla pagenum-taggar under hx -->
  <xsl:for-each select="descendant::pagenum">
    <xsl:copy-of select="."/>
  </xsl:for-each>
  <xsl:call-template name="copy"/>
</xsl:template>

<!-- behandla inte pagenum innuti hx -->
<xsl:template match="pagenum[ancestor::h1|ancestor::h2|ancestor::h3|ancestor::h4|ancestor::h5|ancestor::h6|ancestor::hd]"/>

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
