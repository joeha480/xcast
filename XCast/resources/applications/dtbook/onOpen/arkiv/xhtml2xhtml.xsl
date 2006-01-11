<?xml version="1.0" encoding="UTF-8"?>
<!--
  Notefix, tar bort alla idref och id ur Daisy2.02 noter
  -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:output omit-xml-declaration="no" indent="yes"/>

<xsl:template match="span[@class='noteref']">
  <span>
  <xsl:for-each select="@*[not(self::idref)]">
    <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
  </xsl:for-each><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="div[@class='notebody']">
	<div>
		<xsl:for-each select="@*[not(self::id)]">
			<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
		</xsl:for-each><xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="*">
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="copy">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*">
  	  <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>