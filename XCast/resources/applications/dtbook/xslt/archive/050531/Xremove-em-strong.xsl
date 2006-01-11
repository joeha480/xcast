<?xml version="1.0"?>
<!--
		Remove em och strong
		Tar bort alla förekomster av em och strong som har en lista som förfader vars förälder är en levelx med class=toc.
		Joel Håkansson, TPB
		Version 2005-05-09
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no"/>

<xsl:template match="em|strong">
  <xsl:choose>
    <xsl:when test="ancestor::list"><!--[(parent::level|parent::level1|parent::level2|
    								parent::level3|parent::level4|parent::level5|parent::level6)[@class='toc']]-->
      <xsl:apply-templates/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="copy"/>
    </xsl:otherwise>
  </xsl:choose>
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
