
export function getUserAuthHeader() {
  return 'eyJ1c2VybmFtZSI6InVzZXIiLCJpZCI6MiwiZW1haWwiOiJ1c2VyQHVzZXIubG9jYWwiLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0=';
}

export function getAdminAuthHeader() {
  return 'eyJ1c2VybmFtZSI6ImFkbWluIiwiaWQiOjEsImVtYWlsIjoiYWRtaW5AYWRtaW4ubG9jYWwiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl19';
}

export const siteRequest = {
  name: "site",
  url: "siteurl",
  categoryIds: [1],
  selector: {
    base: "#normal-product-list .product-box-container",
    image: {
      selector: ".image img",
      property: "src"
    },
    price: {
      selector: ".price",
      property: "innerText"
    },
    title: {
      selector: ".name h2 a",
      property: "innerText"
    },
    url: {
      selector: ".name h2 a",
      property: "href"
    }
  }
}
