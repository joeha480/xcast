<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="no"/>

<xsl:template match="h4">
  <xsl:choose>
  <xsl:when test="parent::level4">
    <xsl:call-template name="level"/>
  </xsl:when>
  <xsl:otherwise>
    <level4>
      <xsl:call-template name="level"/>
    </level4>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="*">
  <xsl:if test="count(preceding-sibling::h4)=0">
    <xsl:call-template name="copy"/>
  </xsl:if>
</xsl:template>

<xsl:template name="level">
  <xsl:call-template name="copy"/>
  <xsl:for-each select="following-sibling::*[generate-id(preceding-sibling::h4[1])=generate-id(current()) and not(self::h4)]">
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
