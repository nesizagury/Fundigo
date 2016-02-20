package com.example.FundigoApp.Events;

import java.util.List;

/**
 * Created by Sprintzin on 16/02/2016.
 */
public class Result {


    /**
     * results : [{"address_components":[{"long_name":"52","short_name":"52","types":["street_number"]},{"long_name":"Ha-Khalutzim Street","short_name":"Ha-Khalutzim St","types":["route"]},{"long_name":"Tel Aviv-Yafo","short_name":"Tel Aviv-Yafo","types":["locality","political"]},{"long_name":"Tel Aviv District","short_name":"Tel Aviv District","types":["administrative_area_level_1","political"]},{"long_name":"Israel","short_name":"IL","types":["country","political"]}],"formatted_address":"Ha-Khalutzim St 52, Tel Aviv-Yafo, Israel","geometry":{"bounds":{"northeast":{"lat":32.0566389,"lng":34.7722894},"southwest":{"lat":32.0566351,"lng":34.7722712}},"location":{"lat":32.0566389,"lng":34.7722712},"location_type":"RANGE_INTERPOLATED","viewport":{"northeast":{"lat":32.0579859802915,"lng":34.7736292802915},"southwest":{"lat":32.0552880197085,"lng":34.7709313197085}}},"place_id":"EjPXlNeX15zXldem15nXnSA1Miwg16rXnCDXkNeR15nXkSDXmdek15UsINeZ16nXqNeQ15w","types":["street_address"]}]
     * status : OK
     */

    private String status;
    /**
     * address_components : [{"long_name":"52","short_name":"52","types":["street_number"]},{"long_name":"Ha-Khalutzim Street","short_name":"Ha-Khalutzim St","types":["route"]},{"long_name":"Tel Aviv-Yafo","short_name":"Tel Aviv-Yafo","types":["locality","political"]},{"long_name":"Tel Aviv District","short_name":"Tel Aviv District","types":["administrative_area_level_1","political"]},{"long_name":"Israel","short_name":"IL","types":["country","political"]}]
     * formatted_address : Ha-Khalutzim St 52, Tel Aviv-Yafo, Israel
     * geometry : {"bounds":{"northeast":{"lat":32.0566389,"lng":34.7722894},"southwest":{"lat":32.0566351,"lng":34.7722712}},"location":{"lat":32.0566389,"lng":34.7722712},"location_type":"RANGE_INTERPOLATED","viewport":{"northeast":{"lat":32.0579859802915,"lng":34.7736292802915},"southwest":{"lat":32.0552880197085,"lng":34.7709313197085}}}
     * place_id : EjPXlNeX15zXldem15nXnSA1Miwg16rXnCDXkNeR15nXkSDXmdek15UsINeZ16nXqNeQ15w
     * types : ["street_address"]
     */

    private List<ResultsEntity> results;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        private String formatted_address;
        /**
         * bounds : {"northeast":{"lat":32.0566389,"lng":34.7722894},"southwest":{"lat":32.0566351,"lng":34.7722712}}
         * location : {"lat":32.0566389,"lng":34.7722712}
         * location_type : RANGE_INTERPOLATED
         * viewport : {"northeast":{"lat":32.0579859802915,"lng":34.7736292802915},"southwest":{"lat":32.0552880197085,"lng":34.7709313197085}}
         */

        private GeometryEntity geometry;
        private String place_id;
        /**
         * long_name : 52
         * short_name : 52
         * types : ["street_number"]
         */

        private List<AddressComponentsEntity> address_components;
        private List<String> types;

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public void setGeometry(GeometryEntity geometry) {
            this.geometry = geometry;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public void setAddress_components(List<AddressComponentsEntity> address_components) {
            this.address_components = address_components;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public GeometryEntity getGeometry() {
            return geometry;
        }

        public String getPlace_id() {
            return place_id;
        }

        public List<AddressComponentsEntity> getAddress_components() {
            return address_components;
        }

        public List<String> getTypes() {
            return types;
        }

        public static class GeometryEntity {
            /**
             * northeast : {"lat":32.0566389,"lng":34.7722894}
             * southwest : {"lat":32.0566351,"lng":34.7722712}
             */

            private BoundsEntity bounds;
            /**
             * lat : 32.0566389
             * lng : 34.7722712
             */

            private LocationEntity location;
            private String location_type;
            /**
             * northeast : {"lat":32.0579859802915,"lng":34.7736292802915}
             * southwest : {"lat":32.0552880197085,"lng":34.7709313197085}
             */

            private ViewportEntity viewport;

            public void setBounds(BoundsEntity bounds) {
                this.bounds = bounds;
            }

            public void setLocation(LocationEntity location) {
                this.location = location;
            }

            public void setLocation_type(String location_type) {
                this.location_type = location_type;
            }

            public void setViewport(ViewportEntity viewport) {
                this.viewport = viewport;
            }

            public BoundsEntity getBounds() {
                return bounds;
            }

            public LocationEntity getLocation() {
                return location;
            }

            public String getLocation_type() {
                return location_type;
            }

            public ViewportEntity getViewport() {
                return viewport;
            }

            public static class BoundsEntity {
                /**
                 * lat : 32.0566389
                 * lng : 34.7722894
                 */

                private NortheastEntity northeast;
                /**
                 * lat : 32.0566351
                 * lng : 34.7722712
                 */

                private SouthwestEntity southwest;

                public void setNortheast(NortheastEntity northeast) {
                    this.northeast = northeast;
                }

                public void setSouthwest(SouthwestEntity southwest) {
                    this.southwest = southwest;
                }

                public NortheastEntity getNortheast() {
                    return northeast;
                }

                public SouthwestEntity getSouthwest() {
                    return southwest;
                }

                public static class NortheastEntity {
                    private double lat;
                    private double lng;

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }

                    public double getLat() {
                        return lat;
                    }

                    public double getLng() {
                        return lng;
                    }
                }

                public static class SouthwestEntity {
                    private double lat;
                    private double lng;

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }

                    public double getLat() {
                        return lat;
                    }

                    public double getLng() {
                        return lng;
                    }
                }
            }

            public static class LocationEntity {
                private double lat;
                private double lng;

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }

                public double getLat() {
                    return lat;
                }

                public double getLng() {
                    return lng;
                }
            }

            public static class ViewportEntity {
                /**
                 * lat : 32.0579859802915
                 * lng : 34.7736292802915
                 */

                private NortheastEntity northeast;
                /**
                 * lat : 32.0552880197085
                 * lng : 34.7709313197085
                 */

                private SouthwestEntity southwest;

                public void setNortheast(NortheastEntity northeast) {
                    this.northeast = northeast;
                }

                public void setSouthwest(SouthwestEntity southwest) {
                    this.southwest = southwest;
                }

                public NortheastEntity getNortheast() {
                    return northeast;
                }

                public SouthwestEntity getSouthwest() {
                    return southwest;
                }

                public static class NortheastEntity {
                    private double lat;
                    private double lng;

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }

                    public double getLat() {
                        return lat;
                    }

                    public double getLng() {
                        return lng;
                    }
                }

                public static class SouthwestEntity {
                    private double lat;
                    private double lng;

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }

                    public double getLat() {
                        return lat;
                    }

                    public double getLng() {
                        return lng;
                    }
                }
            }
        }

        public static class AddressComponentsEntity {
            private String long_name;
            private String short_name;
            private List<String> types;

            public void setLong_name(String long_name) {
                this.long_name = long_name;
            }

            public void setShort_name(String short_name) {
                this.short_name = short_name;
            }

            public void setTypes(List<String> types) {
                this.types = types;
            }

            public String getLong_name() {
                return long_name;
            }

            public String getShort_name() {
                return short_name;
            }

            public List<String> getTypes() {
                return types;
            }
        }
    }
}
