<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="no"/>

<xsl:template match="h5">
  <xsl:choose>
  <xsl:when test="parent::level5">
    <xsl:call-template name="level"/>
  </xsl:when>
  <xsl:otherwise>
    <level5>
      <xsl:call-template name="level"/>
    </level5>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="*">
  <xsl:if test="count(preceding-sibling::h5)=0">
    <xsl:call-template name="copy"/>
  </xsl:if>
</xsl:template>

<xsl:template name="level">
  <xsl:call-template name="copy"/>
  <xsl:for-each select="following-sibling::*[generate-id(preceding-sibling::h5[1])=generate-id(current()) and not(self::h5)]">
    <xsl:call-template name="copy"/>
  </xsl:for-each>
</xsl:template>

<xsl:template name="copy">
    <xsl:element name="{name()}">
  	  <xsl:for-each select="@*"><xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
       <xsl:apply-templates/>
    </xsl:element>
</xsl:template>

</xsl:stylesheet>
